package at.searles.parsing.parser.combinators

import at.searles.parsing.*
import at.searles.parsing.parser.*
import at.searles.parsing.parser.PrintFailure
import at.searles.parsing.reader.PositionReader

class FoldAppliedToParser<A, B, C>(private val foldAction: FoldAction<A, B, C>, private val parser: Parser<Unit, B>) : Parser<A, C> {
    override fun parse(input: A, reader: PositionReader): ParseResult<C> {
        return when (val result = parser.parse(Unit, reader)) {
            is ParseSuccess -> ParseSuccess(foldAction.fold(input, result.value), result.start, result.end)
            is ParseFailure -> result
        }
    }

    override fun print(value: C): PrintResult<A> {
        return when (val rightInvResult = foldAction.rightInverse(value)) {
            is InvertSuccess -> when (val rightPrintResult = parser.print(rightInvResult.value)) {
                is PrintSuccess -> when (val leftResult = foldAction.leftInverse(value)) {
                    is InvertSuccess -> PrintSuccess(leftResult.value, rightPrintResult.output)
                    is InvertFailure -> PrintFailure
                }
                is PrintFailure -> PrintFailure
            }
            is InvertFailure -> PrintFailure
        }
    }
}
