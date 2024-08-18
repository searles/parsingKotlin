package at.searles.parsing.parser.combinators

import at.searles.parsing.Failure
import at.searles.parsing.Result
import at.searles.parsing.Success
import at.searles.parsing.parser.*
import at.searles.parsing.reader.PositionReader

class ParserThenFold<A, B, C>(private val parser: Parser<B>, private val fold: Fold<A, B, C>) : Reducer<A, C> {
    override fun parse(reader: PositionReader, input: A): Result<C> {
        return when (val result = parser.parse(reader)) {
            is Success -> Success(fold.fold(input, result.value), result.start, result.end)
            else -> Failure
        }
    }
}