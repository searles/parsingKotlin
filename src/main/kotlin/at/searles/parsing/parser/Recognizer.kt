package at.searles.parsing.parser

import at.searles.parsing.parser.combinators.RecognizerThenParser
import at.searles.parsing.reader.IndexedReader

interface Recognizer {
    fun recognize(reader: IndexedReader): Boolean

    operator fun <A> plus(parser: Parser<A>): Parser<A> {
        return RecognizerThenParser(this, parser)
    }
}