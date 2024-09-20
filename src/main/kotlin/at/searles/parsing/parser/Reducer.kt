package at.searles.parsing.parser

import at.searles.parsing.ParseResult
import at.searles.parsing.parser.combinators.ReducerOrReducer
import at.searles.parsing.parser.combinators.RepeatReducer
import at.searles.parsing.reader.PositionReader

interface Reducer<A, B> {
    fun parse(reader: PositionReader, input: A): ParseResult<B>
    fun print(value: B): PartialPrintResult<A>

    infix fun or(reducer: Reducer<A, B>): Reducer<A, B> {
        return ReducerOrReducer(this, reducer)
    }

    companion object {
        inline fun <reified A> Reducer<A, A>.rep(): Reducer<A, A> {
            return RepeatReducer(this)
        }

        inline fun <reified A> Reducer<A, A>.opt(): Reducer<A, A> {
            return RepeatReducer(this)
        }
    }
}