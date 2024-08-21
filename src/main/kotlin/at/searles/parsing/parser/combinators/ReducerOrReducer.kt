package at.searles.parsing.parser.combinators

import at.searles.parsing.ParseFailure
import at.searles.parsing.ParseResult
import at.searles.parsing.ParseSuccess
import at.searles.parsing.parser.PartialPrintFailure
import at.searles.parsing.parser.PartialPrintResult
import at.searles.parsing.parser.PartialPrintSuccess
import at.searles.parsing.parser.Reducer
import at.searles.parsing.reader.PositionReader

class ReducerOrReducer<A, B>(private val reducer0: Reducer<A, B>, private val reducer1: Reducer<A, B>) : Reducer<A, B> {
    override fun parse(reader: PositionReader, input: A): ParseResult<B> {
        return when (val result = reducer0.parse(reader, input)) {
            is ParseSuccess -> result
            is ParseFailure -> reducer1.parse(reader, input)
        }
    }

    override fun print(value: B): PartialPrintResult<A> {
        return when (val result = reducer0.print(value)) {
            is PartialPrintSuccess -> result
            is PartialPrintFailure -> reducer1.print(value)
        }
    }
}