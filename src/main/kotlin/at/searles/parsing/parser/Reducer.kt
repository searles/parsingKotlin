package at.searles.parsing.parser

import at.searles.parsing.Result
import at.searles.parsing.parser.combinators.ReducerOrReducer
import at.searles.parsing.parser.combinators.RepeatReducer
import at.searles.parsing.reader.PositionReader

interface Reducer<A, B> {
    fun parse(reader: PositionReader, input: A): Result<B>

    infix fun or(reducer: Reducer<A, B>): Reducer<A, B> {
        return ReducerOrReducer(this, reducer)
    }

    companion object {
        inline fun <reified A> Reducer<A, A>.rep(): Reducer<A, A> {
            return RepeatReducer(this)
        }
    }
}