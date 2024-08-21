package at.searles.parsing.reader

import at.searles.parsing.ParseResult

interface PositionReader : CodePointReader {
    var position: Long

    fun <A> applyConsumer(consumer: Consumer<A>): ParseResult<A> {
        return consumer.consume(this)
    }

    fun getSequence(start: Long, end: Long): CodePointSequence
}