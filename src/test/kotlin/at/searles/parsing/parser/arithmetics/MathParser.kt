package at.searles.parsing.parser.arithmetics

import at.searles.parsing.lexer.Lexer
import at.searles.parsing.lexer.WithLexer
import at.searles.parsing.lexer.regexp.Plus
import at.searles.parsing.lexer.regexp.Ranges
import at.searles.parsing.parser.*
import at.searles.parsing.parser.Reducer.Companion.rep

object MathParser: WithLexer {
    override val lexer: Lexer = Lexer()

    private val number = Plus(Ranges('0'.code .. '9'.code)).parser + MapAction { it.toReader().fold(0) { value, digit -> value * 10 + digit - '0'.code }  }

    val simpleArithmetic = number + (
            "+".recognizer  + number + FoldAction { left: Int, right: Int -> left + right } or
            "-".recognizer + number + FoldAction { left: Int, right: Int -> left - right }
    )

    val expr: Parser<Int> = self { sum }

    private val terminal = number or
            "(".recognizer + expr + ")".recognizer

    private val literal = self {
            "-".recognizer + it + MapAction { value: Int -> -value } or
            terminal
    }

    private val prod = literal + (
            "*".recognizer + literal + FoldAction { left: Int, right: Int -> left * right } or
            "/".recognizer + literal + FoldAction { left: Int, right: Int -> left / right }
    ).rep()

    private val sum = prod + (
        "+".recognizer + prod + FoldAction { left: Int, right: Int -> left + right } or
        "-".recognizer + prod + FoldAction { left: Int, right: Int -> left - right }
    ).rep()
}