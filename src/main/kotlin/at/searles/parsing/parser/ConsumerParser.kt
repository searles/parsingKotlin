package at.searles.parsing.parser

import at.searles.parsing.Failure
import at.searles.parsing.Result
import at.searles.parsing.Success
import at.searles.parsing.reader.Consumer
import at.searles.parsing.reader.PositionReader

class ConsumerParser<A, B>(private val consumer: Consumer<A>, private val label: A, private val converter: Converter<PositionReader, B>) : Parser<B> {
    override fun parse(reader: PositionReader): Result<B> {
        val checkpoint = reader.position
        val result = reader.applyConsumer(consumer)

        when {
            result is Success && result.value == label -> {
                val value = converter.convert(reader.getReader(result.start, result.end))
                return Success(value, result.start, result.end)
            }
            else -> {
                reader.position = checkpoint
                return Failure
            }
        }
    }
}