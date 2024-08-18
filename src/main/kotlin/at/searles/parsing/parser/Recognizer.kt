package at.searles.parsing.parser

import at.searles.parsing.Result
import at.searles.parsing.parser.combinators.RecognizerThenParser
import at.searles.parsing.reader.PositionReader

interface Recognizer {
    fun parse(reader: PositionReader): Result<Unit>

    operator fun <A> plus(parser: Parser<A>): Parser<A> {
        return RecognizerThenParser(this, parser)
    }
}