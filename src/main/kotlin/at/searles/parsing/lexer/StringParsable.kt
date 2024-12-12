package at.searles.parsing.lexer

import at.searles.parsing.*
import at.searles.parsing.parser.Parser
import at.searles.parsing.parser.PrintSuccess
import at.searles.parsing.reader.StringCodePointReader

class StringParser<A>(private val parser: Parser<Unit, A>) {
    fun fromString(str: String): A? {
        val reader = StringCodePointReader(str)

        return when (val result = parser.parse(Unit, reader)) {
            is ParseFailure -> null
            is ParseSuccess -> if (result.end != str.length.toLong()) {
                null
            } else {
                result.value
            }
        }
    }

    fun toString(a: A): String? {
        return when (val result = parser.print(a)) {
            is PrintSuccess -> result.toString()
            else -> null
        }
    }
}