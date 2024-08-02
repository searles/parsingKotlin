package at.searles.parsing.parser

import at.searles.parsing.parser.combinators.ParserThenFold
import at.searles.parsing.parser.combinators.ParserThenReducer
import at.searles.parsing.reader.IndexedReader

interface Parser<A> {
    fun parse(reader: IndexedReader): ParserResult<A>

    operator fun <B> plus(reducer: Reducer<A, B>): Parser<B> {
        return ParserThenReducer(this, reducer)
    }

    operator fun <A0, C> plus(fold: Fold<A0, A, C>): Reducer<A0, C> {
        return ParserThenFold(this, fold)
    }
}