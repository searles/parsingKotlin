package at.searles.parsing.parser

import at.searles.parsing.PrintResult
import at.searles.parsing.ParseResult
import at.searles.parsing.parser.combinators.RecognizerThenParser
import at.searles.parsing.parser.combinators.RecognizerThenReducer
import at.searles.parsing.reader.PositionReader

interface Recognizer {
    fun parse(reader: PositionReader): ParseResult<Unit>
    fun print(): PrintResult

    operator fun <A> plus(parser: Parser<A>): Parser<A> {
        return RecognizerThenParser(this, parser)
    }

    operator fun <A, B> plus(reducer: Reducer<A, B>): Reducer<A, B> {
        return RecognizerThenReducer(this, reducer)
    }
}