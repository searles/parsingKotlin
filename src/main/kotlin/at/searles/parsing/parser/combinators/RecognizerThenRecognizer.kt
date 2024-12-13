package at.searles.parsing.parser.combinators

import at.searles.parsing.*
import at.searles.parsing.parser.*
import at.searles.parsing.parser.PrintFailure
import at.searles.parsing.reader.PositionReader

class RecognizerThenRecognizer(private val left: Recognizer, private val right: Recognizer): Recognizer {
    override fun parse(reader: PositionReader): ParseResult<Unit> {
        val checkpoint = reader.position

        return when (val leftResult = left.parse( reader)) {
            is ParseSuccess -> when (val rightResult = right.parse(reader)) {
                is ParseSuccess -> ParseSuccess(rightResult.value, leftResult.start, rightResult.end)
                is ParseFailure -> {
                    reader.position = checkpoint
                    rightResult
                }
            }
            is ParseFailure -> leftResult
        }
    }

    override fun print(): PrintResult<Unit> {
        return when (val rightResult = right.print()) {
            is PrintSuccess -> when (val leftResult = left.print()) {
                is PrintSuccess -> PrintSuccess(Unit, leftResult.output + rightResult.output)
                is PrintFailure -> leftResult
            }
            is PrintFailure -> PrintFailure
        }
    }

    override fun toString(): String {
        return "($left + $right)"
    }
}