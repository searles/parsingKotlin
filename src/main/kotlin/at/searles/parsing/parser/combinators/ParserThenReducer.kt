package at.searles.parsing.parser.combinators

import at.searles.parsing.Failure
import at.searles.parsing.parser.Parser
import at.searles.parsing.Result
import at.searles.parsing.Success
import at.searles.parsing.parser.Reducer
import at.searles.parsing.reader.PositionReader

class ParserThenReducer<A, B>(private val left: Parser<A>, private val right: Reducer<A, B>): Parser<B> {
    override fun parse(reader: PositionReader): Result<B> {
        val checkpoint = reader.position

        return when (val leftResult = left.parse(reader)) {
            is Success -> when (val rightResult = right.parse(reader, leftResult.value)) {
                is Success -> Success(rightResult.value, leftResult.start, rightResult.end)
                is Failure -> {
                    reader.position = checkpoint
                    Failure
                }
            }
            is Failure -> Failure
        }
    }
}