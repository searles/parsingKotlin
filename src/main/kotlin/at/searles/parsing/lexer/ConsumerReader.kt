package at.searles.parsing.lexer

import at.searles.parsing.ParseFailure
import at.searles.parsing.ParseResult
import at.searles.parsing.ParseSuccess
import at.searles.parsing.reader.Consumer
import at.searles.parsing.reader.PositionReader

class ConsumerReader(delegate: PositionReader): PositionReader by delegate {
    private var lastConsumer: Consumer<*>? = null
    private lateinit var lastResult: ParseResult<*>

    override fun <A> accept(consumer: Consumer<A>): ParseResult<A> {
        if (lastConsumer == consumer && lastResult.position == position) {
            return when (lastResult) {
                is ParseSuccess -> run {
                    position = (lastResult as ParseSuccess).end
                    lastResult as ParseSuccess<A>
                }
                is ParseFailure -> lastResult as ParseFailure
            }
        }

        val result = super.accept(consumer)
        lastConsumer = consumer
        lastResult = result

        return result
    }
}