package at.searles.parsing.parser.combinators

import at.searles.parsing.*
import at.searles.parsing.parser.Parser
import at.searles.parsing.reader.PositionReader

class ParserOrParser<A>(private val parser0: Parser<A>, private val parser1: Parser<A>): Parser<A> {
    override fun parse(reader: PositionReader): ParseResult<A> {
        return when (val result = parser0.parse(reader)) {
            is ParseSuccess -> result
            is ParseFailure -> parser1.parse(reader)
        }
    }

    override fun print(value: A): PrintResult {
        return when (val result = parser0.print(value)) {
            is PrintSuccess -> result
            is PrintFailure -> parser1.print(value)
        }
    }
}