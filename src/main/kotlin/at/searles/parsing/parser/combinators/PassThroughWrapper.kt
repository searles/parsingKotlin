package at.searles.parsing.parser.combinators

import at.searles.parsing.ParseFailure
import at.searles.parsing.ParseResult
import at.searles.parsing.ParseSuccess
import at.searles.parsing.parser.*
import at.searles.parsing.reader.PositionReader

class PassThroughWrapper<A>(private val recognizer: Recognizer) : Parser<A, A> {
    override fun parse(input: A, reader: PositionReader): ParseResult<A> {
        return when (val result = recognizer.parse(reader)) {
            is ParseSuccess -> ParseSuccess(input, result.start, result.end)
            is ParseFailure -> ParseFailure(result.position)
        }
    }

    override fun print(value: A): PrintResult<A> {
        return when (val result = recognizer.print()) {
            is PrintSuccess -> PrintSuccess(value, result.output)
            is PrintFailure -> PrintFailure
        }
    }

    override fun toString(): String {
        return recognizer.toString()
    }
}
