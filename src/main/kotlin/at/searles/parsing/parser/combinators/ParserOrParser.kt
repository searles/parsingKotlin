package at.searles.parsing.parser.combinators

import at.searles.parsing.Failure
import at.searles.parsing.Result
import at.searles.parsing.Success
import at.searles.parsing.parser.Parser
import at.searles.parsing.reader.PositionReader

class ParserOrParser<A>(private val parser0: Parser<A>, private val parser1: Parser<A>): Parser<A> {
    override fun parse(reader: PositionReader): Result<A> {
        return when (val result = parser0.parse(reader)) {
            is Success -> result
            is Failure -> parser1.parse(reader)
        }
    }
}