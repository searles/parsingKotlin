package at.searles.parsing.parser

import at.searles.parsing.*
import at.searles.parsing.reader.PositionReader

fun interface MapAction<A, B>: Reducer<A, B> {
    fun convert(value: A): B
    fun invert(result: B): InvertResult<A> {
        error("No inverse defined")
    }

    override fun parse(reader: PositionReader, input: A): ParseResult<B> {
        return ParseSuccess(convert(input), reader.position, reader.position)
    }

    override fun print(value: B): PartialPrintResult<A> {
        return when (val result = invert(value)) {
            is InvertSuccess -> PartialPrintSuccess(result.value, EmptyPrintSuccess)
            InvertFailure  -> PartialPrintFailure
        }
    }
}