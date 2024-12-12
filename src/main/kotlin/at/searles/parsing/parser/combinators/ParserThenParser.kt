package at.searles.parsing.parser.combinators

import at.searles.parsing.*
import at.searles.parsing.parser.*
import at.searles.parsing.parser.PrintFailure
import at.searles.parsing.reader.PositionReader

class ParserThenParser<A, B, C>(private val left: Parser<A, B>, private val right: Parser<B, C>): Parser<A, C> {
    override fun parse(input: A, reader: PositionReader): ParseResult<C> {
        val checkpoint = reader.position

        return when (val leftResult = left.parse(input, reader)) {
            is ParseSuccess -> when (val rightResult = right.parse(leftResult.value, reader)) {
                is ParseSuccess -> ParseSuccess(rightResult.value, leftResult.start, rightResult.end)
                is ParseFailure -> {
                    reader.position = checkpoint
                    rightResult
                }
            }
            is ParseFailure -> leftResult
        }
    }

    override fun print(value: C): PrintResult<A> {
        return when (val rightResult = right.print(value)) {
            is PrintSuccess -> when (val leftResult = left.print(rightResult.value)) {
                is PrintSuccess -> PrintSuccess(leftResult.value, leftResult.output + rightResult.output)
                is PrintFailure -> leftResult
            }
            is PrintFailure -> PrintFailure
        }
    }
}