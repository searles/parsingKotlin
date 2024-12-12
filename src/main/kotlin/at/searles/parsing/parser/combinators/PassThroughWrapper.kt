package at.searles.parsing.parser.combinators

import at.searles.parsing.ParseFailure
import at.searles.parsing.ParseResult
import at.searles.parsing.ParseSuccess
import at.searles.parsing.parser.Parser
import at.searles.parsing.parser.PrintFailure
import at.searles.parsing.parser.PrintResult
import at.searles.parsing.parser.PrintSuccess
import at.searles.parsing.reader.PositionReader

class PassThroughWrapper<A>(private val parser: Parser<Unit, Unit>) : Parser<A, A> {
    override fun parse(input: A, reader: PositionReader): ParseResult<A> {
        return when (val result = parser.parse(Unit, reader)) {
            is ParseSuccess -> ParseSuccess(input, result.start, result.end)
            is ParseFailure -> ParseFailure(result.position)
        }
    }

    override fun print(value: A): PrintResult<A> {
        return when (val result = parser.print(Unit)) {
            is PrintSuccess -> PrintSuccess(value, result.output)
            is PrintFailure -> PrintFailure
        }
    }
}
