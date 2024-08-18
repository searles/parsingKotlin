package at.searles.parsing.parser.combinators

import at.searles.parsing.Failure
import at.searles.parsing.Result
import at.searles.parsing.Success
import at.searles.parsing.parser.Reducer
import at.searles.parsing.reader.PositionReader

class ReducerOrReducer<A, B>(private val reducer0: Reducer<A, B>, private val reducer1: Reducer<A, B>) : Reducer<A, B> {
    override fun parse(reader: PositionReader, input: A): Result<B> {
        return when (val result = reducer0.parse(reader, input)) {
            is Success -> result
            is Failure -> reducer1.parse(reader, input)
        }
    }
}