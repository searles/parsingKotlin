package at.searles.parsing.parser.combinators

import at.searles.parsing.*
import at.searles.parsing.parser.Parser
import at.searles.parsing.parser.PrintFailure
import at.searles.parsing.parser.PrintResult
import at.searles.parsing.parser.PrintSuccess
import at.searles.parsing.reader.PositionReader

class ParserOrParser<A, B>(private val parser0: Parser<A, B>, private val parser1: Parser<A, B>): Parser<A, B> {
    override fun parse(input: A, reader: PositionReader): ParseResult<B> {
        return when (val result = parser0.parse(input, reader)) {
            is ParseSuccess -> result
            is ParseFailure -> parser1.parse(input, reader)
        }
    }

    override fun print(value: B): PrintResult<A> {
        return when (val result = parser0.print(value)) {
            is PrintSuccess -> result
            is PrintFailure -> parser1.print(value)
        }
    }

    override fun toString(): String {
        return "($parser0 | $parser1)"
    }
}