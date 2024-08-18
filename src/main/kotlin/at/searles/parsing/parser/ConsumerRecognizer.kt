package at.searles.parsing.parser

import at.searles.parsing.Failure
import at.searles.parsing.Result
import at.searles.parsing.Success
import at.searles.parsing.reader.Consumer
import at.searles.parsing.reader.PositionReader

class ConsumerRecognizer<A>(private val consumer: Consumer<A>, private val label: A): Recognizer {
    override fun parse(reader: PositionReader): Result<Unit> {
        val checkpoint = reader.position
        val result = reader.applyConsumer(consumer)

        when {
            result is Success && result.value == label -> {
                return Success(Unit, result.start, result.end)
            }
            else -> {
                reader.position = checkpoint
                return Failure
            }
        }
    }
}
