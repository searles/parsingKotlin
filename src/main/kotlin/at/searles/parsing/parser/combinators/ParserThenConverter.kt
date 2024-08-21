package at.searles.parsing.parser.combinators

import at.searles.parsing.*
import at.searles.parsing.parser.*
import at.searles.parsing.reader.PositionReader

class ParserThenConverter<A, B>(private val parser: Parser<A>, private val converter: Converter<A, B>): Parser<B> {
    override fun parse(reader: PositionReader): ParseResult<B> {
        return when (val result = parser.parse(reader)) {
            is ParseSuccess -> ParseSuccess(converter.convert(result.value), result.start, result.end)
            is ParseFailure -> result
        }
    }

    override fun print(value: B): PrintResult {
        return when (val result = converter.invert(value)) {
            is InvertSuccess -> parser.print(result.value)
            is InvertFailure -> PrintFailure
        }
    }
}