package at.searles.parsing.parser.combinators

import at.searles.parsing.*
import at.searles.parsing.parser.*
import at.searles.parsing.reader.PositionReader

class ParserThenConverter<A, B>(private val parser: Parser<A>, private val mapAction: MapAction<A, B>): Parser<B> {
    override fun parse(reader: PositionReader): ParseResult<B> {
        return when (val result = parser.parse(reader)) {
            is ParseSuccess -> ParseSuccess(mapAction.convert(result.value), result.start, result.end)
            is ParseFailure -> result
        }
    }

    override fun print(value: B): PrintResult {
        return when (val result = mapAction.invert(value)) {
            is InvertSuccess -> parser.print(result.value)
            is InvertFailure -> PrintFailure
        }
    }
}