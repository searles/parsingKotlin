package at.searles.parsing.parser.combinators

import at.searles.parsing.*
import at.searles.parsing.parser.Parser
import at.searles.parsing.parser.PartialPrintFailure
import at.searles.parsing.parser.PartialPrintSuccess
import at.searles.parsing.parser.Recognizer
import at.searles.parsing.reader.PositionReader

class ParserThenRecognizer<A>(private val left: Parser<A>, private val right: Recognizer): Parser<A> {
    override fun parse(reader: PositionReader): ParseResult<A> {
        val checkpoint = reader.position

        return when (val leftResult = left.parse(reader)) {
            is ParseSuccess -> when (val rightResult = right.parse(reader)) {
                is ParseSuccess -> ParseSuccess(leftResult.value, leftResult.start, rightResult.end)
                is ParseFailure -> {
                    reader.position = checkpoint
                    rightResult
                }
            }
            is ParseFailure -> leftResult
        }
    }

    override fun print(value: A): PrintResult {
        return when (val rightResult = right.print()) {
            is PrintSuccess -> when (val leftResult = left.print(value)) {
                is PrintSuccess -> leftResult + rightResult
                is PrintFailure -> leftResult
            }
            is PrintFailure -> PrintFailure
        }
    }
}
