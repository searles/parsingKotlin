package at.searles.parsing.parser.arithmetics

import at.searles.parsing.parser.*
import at.searles.parsing.parser.Reducer.Companion.rep
import at.searles.parsing.reader.CodePointSequence

object MathParser {
    private val number = ConsumerParser(SyntaxConsumer, SyntaxLabel.Number) {
        parseNumber(it)
    }

    val simpleArithmetic = number + (
            kw(SyntaxLabel.Plus)  + number + Fold { left: Int, right: Int -> left + right } or
            kw(SyntaxLabel.Minus) + number + Fold { left: Int, right: Int -> left - right }
    )

    val expr: Parser<Int> = self { sum }

    private val terminal = number or
            kw(SyntaxLabel.Open) + expr + kw(SyntaxLabel.Close)

    private val literal = self {
            kw(SyntaxLabel.Minus) + it + Converter { value: Int -> -value } or
            terminal
    }

    private val prod = literal + (
            kw(SyntaxLabel.Times) + literal + Fold { left: Int, right: Int -> left * right } or
            kw(SyntaxLabel.Div) + literal + Fold { left: Int, right: Int -> left / right }
    ).rep()

    private val sum = prod + (
        kw(SyntaxLabel.Plus) + prod + Fold { left: Int, right: Int -> left + right } or
        kw(SyntaxLabel.Minus) + prod + Fold { left: Int, right: Int -> left - right }
    ).rep()

    fun parseNumber(seq: CodePointSequence): Int {
        var n = 0
        var index = 0
        var cp = seq[index]
        while (cp != -1) {
            n = n * 10 + cp - '0'.code
            cp = seq[++index]
        }
        return n
    }

    fun kw(label: SyntaxLabel): Recognizer {
        return ConsumerRecognizer(SyntaxConsumer, label)
    }
}