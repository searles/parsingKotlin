package at.searles.parsing.parser.combinators

import at.searles.parsing.*
import at.searles.parsing.parser.Parser
import at.searles.parsing.parser.PartialPrintFailure
import at.searles.parsing.parser.PartialPrintSuccess
import at.searles.parsing.parser.Reducer
import at.searles.parsing.reader.PositionReader

class ParserThenReducer<A, B>(private val left: Parser<A>, private val right: Reducer<A, B>): Parser<B> {
    override fun parse(reader: PositionReader): ParseResult<B> {
        val checkpoint = reader.position

        return when (val leftResult = left.parse(reader)) {
            is ParseSuccess -> when (val rightResult = right.parse(reader, leftResult.value)) {
                is ParseSuccess -> ParseSuccess(rightResult.value, leftResult.start, rightResult.end)
                is ParseFailure -> {
                    reader.position = checkpoint
                    rightResult
                }
            }
            is ParseFailure -> leftResult
        }
    }

    override fun print(value: B): PrintResult {
        return when (val rightResult = right.print(value)) {
            is PartialPrintSuccess -> when (val leftResult = left.print(rightResult.left)) {
                is PrintSuccess -> leftResult + rightResult.right
                is PrintFailure -> leftResult
            }
            is PartialPrintFailure -> PrintFailure
        }
    }
}