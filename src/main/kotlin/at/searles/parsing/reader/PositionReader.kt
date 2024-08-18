package at.searles.parsing.reader

import at.searles.parsing.Result

interface PositionReader : CodePointReader {
    var position: Long

    fun <A> applyConsumer(consumer: Consumer<A>): Result<A> {
        return consumer.consume(this)
    }

    fun getReader(start: Long, end: Long): PositionReader
}