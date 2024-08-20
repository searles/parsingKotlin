package at.searles.parsing.parser.arithmetics

import at.searles.parsing.parser.*
import at.searles.parsing.parser.Reducer.Companion.rep
import at.searles.parsing.reader.CodePointReader

object ArithmeticParser {
    private val number = ConsumerParser(ArithmeticSyntaxConsumer, SyntaxLabel.Number) {
        parseNumber(it)
    }

    val simpleArithmetic = number + (
            kw(SyntaxLabel.Plus)  + number + Fold { left: Int, right: Int -> left + right } or
            kw(SyntaxLabel.Minus) + number + Fold { left: Int, right: Int -> left - right }
    )

    val expr: Parser<Int> = LazyParser { sum }

    val terminal = number or
            kw(SyntaxLabel.Open) + expr + kw(SyntaxLabel.Close)

    val literal = LazyParser {
        kw(SyntaxLabel.Minus) + it + Converter { value: Int -> -value } or
            terminal
    }

    val prod = literal + (
            kw(SyntaxLabel.Times) + literal + Fold { left: Int, right: Int -> left * right } or
            kw(SyntaxLabel.Div) + literal + Fold { left: Int, right: Int -> left / right }
    ).rep()

    val sum = prod +
            (
                kw(SyntaxLabel.Plus) + prod + Fold { left: Int, right: Int -> left + right } or
                kw(SyntaxLabel.Minus) + prod + Fold { left: Int, right: Int -> left - right }
            ).rep()

    private fun parseNumber(reader: CodePointReader): Int {
        var n = 0
        var cp = reader.read()
        while (cp != -1) {
            n = n * 10 + cp - '0'.code
            cp = reader.read()
        }
        return n
    }

    private fun kw(label: SyntaxLabel): Recognizer {
        return ConsumerRecognizer(ArithmeticSyntaxConsumer, label)
    }
}