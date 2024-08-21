package at.searles.parsing.parser

import at.searles.parsing.*
import at.searles.parsing.reader.CodePointSequence
import at.searles.parsing.reader.Consumer
import at.searles.parsing.reader.PositionReader

// Temporary class, I do not expect to use it that much
class ConsumerParser<A, B>(private val consumer: Consumer<A>, private val label: A, private val converter: Converter<CodePointSequence, B>) : Parser<B> {
    override fun parse(reader: PositionReader): ParseResult<B> {
        val checkpoint = reader.position
        val result = reader.applyConsumer(consumer)

        when {
            result is ParseSuccess && result.value == label -> {
                val value = converter.convert(reader.getSequence(result.start, result.end))
                return ParseSuccess(value, result.start, result.end)
            }
            else -> {
                reader.position = checkpoint
                return ParseFailure(checkpoint, "Missing $label")
            }
        }
    }

    override fun print(value: B): PrintResult {
        return when (val result = converter.invert(value)) {
            is InvertSuccess -> consumer.print(label, result.value)
            is InvertFailure -> PrintFailure
        }
    }
}
