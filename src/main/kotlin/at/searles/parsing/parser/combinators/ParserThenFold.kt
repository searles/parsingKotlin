package at.searles.parsing.parser.combinators

import at.searles.parsing.*
import at.searles.parsing.parser.*
import at.searles.parsing.reader.PositionReader

class ParserThenFold<A, B, C>(private val parser: Parser<B>, private val fold: Fold<A, B, C>) : Reducer<A, C> {
    override fun parse(reader: PositionReader, input: A): ParseResult<C> {
        return when (val result = parser.parse(reader)) {
            is ParseSuccess -> ParseSuccess(fold.fold(input, result.value), result.start, result.end)
            is ParseFailure -> result
        }
    }

    override fun print(value: C): PartialPrintResult<A> {
        return when (val rightResult = fold.rightInverse(value)) {
            is InvertSuccess -> when (val rightPrintResult = parser.print(rightResult.value)) {
                is PrintSuccess -> when (val leftResult = fold.leftInverse(value)) {
                    is InvertSuccess -> PartialPrintSuccess(leftResult.value, rightPrintResult)
                    is InvertFailure -> PartialPrintFailure
                }
                is PrintFailure -> PartialPrintFailure
            }
            is InvertFailure -> PartialPrintFailure
        }
    }
}
