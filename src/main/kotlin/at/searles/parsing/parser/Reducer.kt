package at.searles.parsing.parser

import at.searles.parsing.parser.combinators.ReducerOrReducer
import at.searles.parsing.reader.IndexedReader

interface Reducer<A, B> {
    fun parse(reader: IndexedReader, input: A): ParserResult<B>

    infix fun or(reducer: Reducer<A, B>): Reducer<A, B> {
        return ReducerOrReducer(this, reducer)
    }
}