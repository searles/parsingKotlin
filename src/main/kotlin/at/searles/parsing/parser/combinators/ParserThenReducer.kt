package at.searles.parsing.parser.combinators

import at.searles.parsing.parser.Parser
import at.searles.parsing.parser.ParserResult
import at.searles.parsing.parser.Reducer
import at.searles.parsing.reader.IndexedReader

class ParserThenReducer<A, B>(private val left: Parser<A>, private val right: Reducer<A, B>): Parser<B> {
    override fun parse(reader: IndexedReader): ParserResult<B> {
        val startIndex = reader.index
        val leftResult = left.parse(reader)

        if (!leftResult.isSuccess) {
            return ParserResult.failure()
        }

        val rightResult = right.parse(reader, leftResult.value)

        if (!rightResult.isSuccess) {
            reader.index = startIndex
        }

        return rightResult
    }
}