package at.searles.parsing.parser.combinators

import at.searles.parsing.Failure
import at.searles.parsing.Result
import at.searles.parsing.Success
import at.searles.parsing.parser.Reducer
import at.searles.parsing.reader.PositionReader

class RepeatReducer<A>(private val reducer: Reducer<A, A>): Reducer<A, A> {
    override fun parse(reader: PositionReader, input: A): Result<A> {
        var value = input
        val start = reader.position
        var end = start

        while(true) {
            when (val result = reducer.parse(reader, value)) {
                is Success -> {
                    value = result.value
                    end = result.end
                }
                is Failure -> {
                    return Success(value, start, end)
                }
            }
        }
    }
}