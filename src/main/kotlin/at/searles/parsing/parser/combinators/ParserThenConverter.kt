package at.searles.parsing.parser.combinators

import at.searles.parsing.Failure
import at.searles.parsing.Result
import at.searles.parsing.Success
import at.searles.parsing.parser.*
import at.searles.parsing.reader.PositionReader

class ParserThenConverter<A, B>(private val parser: Parser<A>, private val converter: Converter<A, B>): Parser<B> {
    override fun parse(reader: PositionReader): Result<B> {
        return when (val result = parser.parse(reader)) {
            is Success -> Success(converter.convert(result.value), result.start, result.end)
            else -> Failure
        }
    }
}