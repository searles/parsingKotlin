package at.searles.parsing.parser.combinators

import at.searles.parsing.*
import at.searles.parsing.parser.*
import at.searles.parsing.reader.PositionReader

class RecognizerThenReducer<A, B>(private val left: Recognizer, private val right: Reducer<A, B>): Reducer<A, B> {
    override fun parse(reader: PositionReader, input: A): ParseResult<B> {
        val checkpoint = reader.position

        return when (val leftResult = left.parse(reader)) {
            is ParseSuccess -> when (val rightResult = right.parse(reader, input)) {
                is ParseSuccess -> ParseSuccess(rightResult.value, leftResult.start, rightResult.end)
                is ParseFailure -> {
                    reader.position = checkpoint
                    rightResult
                }
            }
            is ParseFailure -> leftResult
        }
    }

    override fun print(value: B): PartialPrintResult<A> {
        return when (val rightResult = right.print(value)) {
            is PartialPrintSuccess -> when (val leftResult = left.print()) {
                is PrintSuccess -> PartialPrintSuccess<A>(rightResult.left, leftResult + rightResult.right)
                is PrintFailure -> PartialPrintFailure
            }
            is PartialPrintFailure -> PartialPrintFailure
        }
    }
}