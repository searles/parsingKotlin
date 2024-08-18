package at.searles.parsing.parser.arithmetics

import at.searles.parsing.parser.ConsumerParser
import at.searles.parsing.parser.ConsumerRecognizer
import at.searles.parsing.parser.Fold
import at.searles.parsing.parser.Recognizer
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

    val arithmetic = number + (
            kw(SyntaxLabel.Plus) + number + Fold { left: Int, right: Int -> left + right } or
            kw(SyntaxLabel.Minus) + number + Fold { left: Int, right: Int -> left - right }
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