package at.searles.parsing.parser

import at.searles.parsing.ParseFailure
import at.searles.parsing.PrintResult
import at.searles.parsing.ParseResult
import at.searles.parsing.ParseSuccess
import at.searles.parsing.reader.Consumer
import at.searles.parsing.reader.PositionReader

class ConsumerRecognizer<A>(private val consumer: Consumer<A>, private val label: A): Recognizer {
    override fun parse(reader: PositionReader): ParseResult<Unit> {
        val checkpoint = reader.position
        val result = reader.applyConsumer(consumer)

        when {
            result is ParseSuccess && result.value == label -> {
                return ParseSuccess(Unit, result.start, result.end)
            }
            else -> {
                reader.position = checkpoint
                return ParseFailure(checkpoint, "Missing $label")
            }
        }
    }

    override fun print(): PrintResult {
        return consumer.print(label)
    }
}
