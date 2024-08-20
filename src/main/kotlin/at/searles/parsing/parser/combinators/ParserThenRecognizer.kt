package at.searles.parsing.parser.combinators

import at.searles.parsing.Failure
import at.searles.parsing.parser.Parser
import at.searles.parsing.Result
import at.searles.parsing.Success
import at.searles.parsing.parser.Recognizer
import at.searles.parsing.reader.PositionReader

class ParserThenRecognizer<A>(private val left: Parser<A>, private val right: Recognizer): Parser<A> {
    override fun parse(reader: PositionReader): Result<A> {
        val checkpoint = reader.position

        return when (val leftResult = left.parse(reader)) {
            is Success -> when (val rightResult = right.parse(reader)) {
                is Success -> Success(leftResult.value, leftResult.start, rightResult.end)
                is Failure -> {
                    reader.position = checkpoint
                    Failure
                }
            }
            is Failure -> Failure
        }
    }
}
