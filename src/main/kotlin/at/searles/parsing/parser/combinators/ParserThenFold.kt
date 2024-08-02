package at.searles.parsing.parser.combinators

import at.searles.parsing.parser.Fold
import at.searles.parsing.parser.Parser
import at.searles.parsing.parser.ParserResult
import at.searles.parsing.parser.Reducer
import at.searles.parsing.reader.IndexedReader

class ParserThenFold<A, B, C>(private val parser: Parser<B>, private val fold: Fold<A, B, C>) : Reducer<A, C> {
    override fun parse(reader: IndexedReader, input: A): ParserResult<C> {
        val result = parser.parse(reader)
        return if (result.isSuccess) {
            ParserResult.success(fold.fold(input, result.value))
        } else {
            ParserResult.failure()
        }
    }
}