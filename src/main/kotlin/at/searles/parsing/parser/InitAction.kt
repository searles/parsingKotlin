package at.searles.parsing.parser

import at.searles.parsing.*
import at.searles.parsing.reader.PositionReader

fun interface InitAction<A>: Parser<A> {
    fun init(): A

    fun invert(result: A): InvertResult<Unit> {
        error("Not supported")
    }

    override fun parse(reader: PositionReader): ParseResult<A> {
        return ParseSuccess(init(), reader.position, reader.position)
    }

    override fun print(value: A): PrintResult {
        return when (invert(value)) {
            is InvertSuccess -> PrintSuccess.empty()
            is InvertFailure -> PrintFailure
        }
    }
}