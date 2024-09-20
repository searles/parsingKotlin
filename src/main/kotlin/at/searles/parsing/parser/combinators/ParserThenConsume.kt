package at.searles.parsing.parser.combinators

import at.searles.parsing.ParseFailure
import at.searles.parsing.ParseResult
import at.searles.parsing.ParseSuccess
import at.searles.parsing.PrintResult
import at.searles.parsing.parser.ConsumeAction
import at.searles.parsing.parser.Parser
import at.searles.parsing.parser.Recognizer
import at.searles.parsing.reader.PositionReader

class ParserThenConsume<A>(private val parser: Parser<A>, private val consumeAction: ConsumeAction<A>) : Recognizer {
    override fun parse(reader: PositionReader): ParseResult<Unit> {
        return when(val result = parser.parse(reader)) {
            is ParseSuccess -> ParseSuccess(Unit, result.start, result.end)
            is ParseFailure -> result
        }
    }

    override fun print(): PrintResult {
        return parser.print(consumeAction.invert())
    }
}
