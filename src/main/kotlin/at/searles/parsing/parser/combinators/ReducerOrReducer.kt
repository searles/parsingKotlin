package at.searles.parsing.parser.combinators

import at.searles.parsing.parser.ParserResult
import at.searles.parsing.parser.Reducer
import at.searles.parsing.reader.IndexedReader

class ReducerOrReducer<A, B>(private val reducer0: Reducer<A, B>, private val reducer1: Reducer<A, B>) : Reducer<A, B> {
    override fun parse(reader: IndexedReader, input: A): ParserResult<B> {
        val result0 = reducer0.parse(reader, input)

        if (result0.isSuccess) {
            return result0
        }

        return reducer1.parse(reader, input)
    }
}