package at.searles.parsing.parser.combinators

import at.searles.parsing.parser.Parser
import at.searles.parsing.parser.ParserResult
import at.searles.parsing.parser.Recognizer
import at.searles.parsing.reader.IndexedReader

class RecognizerThenParser<A>(private val left: Recognizer, private val right: Parser<A>): Parser<A> {
    override fun parse(reader: IndexedReader): ParserResult<A> {
        val startIndex = reader.index

        if (!left.recognize(reader)) return ParserResult.failure()

        val result = right.parse(reader)

        if (!result.isSuccess) {
            reader.index = startIndex
        }

        return result
    }
}