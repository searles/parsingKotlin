package at.searles.parsing.reader

import at.searles.parsing.ParseResult

interface PositionReader : CodePointReader {
    var position: Long

    fun <A> accept(consumer: Consumer<A>): ParseResult<A> {
        return consumer.consume(this)
    }

    // TODO Add events for eg comments and white chars

    fun getSequence(start: Long, end: Long): CodePointSequence
}