package at.searles.parsing.parser.arithmetics

import at.searles.parsing.lexer.Lexer
import at.searles.parsing.lexer.WithLexer
import at.searles.parsing.lexer.regexp.Plus
import at.searles.parsing.lexer.regexp.Regexp
import at.searles.parsing.parser.*
import at.searles.parsing.parser.Parser.Companion.fold
import at.searles.parsing.parser.Parser.Companion.rep
import at.searles.parsing.reader.CodePointSequence

object MathParser: WithLexer {
    override val lexer: Lexer = Lexer()

    private val number = Plus(Regexp.ranges('0'.code .. '9'.code)).parser + MapAction<CodePointSequence, Int> { it.toReader().fold(0) { value, digit -> value * 10 + digit - '0'.code }  }

    val simpleArithmetic = number + (
            "+".recognizer.passThough<Int>() + number.fold { left: Int, right: Int -> left + right } or
            "-".recognizer.passThough<Int>() + number.fold { left: Int, right: Int -> left - right }
    )

    val expr: Parser<Unit, Int> by ref { sum }

    private val terminal: Parser<Unit, Int> by ref {
        number or
                "(".recognizer + expr + ")".recognizer.passThough()
    }

    private val literal: Parser<Unit, Int> by ref {
            "-".recognizer + literal + MapAction { value: Int -> -value } or
            terminal
    }

    private val prod: Parser<Unit, Int> by ref {
        literal + (
                "*".recognizer.passThough<Int>() + literal.fold { left: Int, right: Int -> left * right } or
                        "/".recognizer.passThough<Int>() + literal.fold { left: Int, right: Int -> left / right }
                ).rep()
    }

    private val sum: Parser<Unit, Int> by ref {
        prod + (
                "+".recognizer.passThough<Int>() + prod.fold { left: Int, right: Int -> left + right } or
                        "-".recognizer.passThough<Int>() + prod.fold { left: Int, right: Int -> left - right }
                ).rep()
    }
}