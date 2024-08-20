package at.searles.parsing.parser

import at.searles.parsing.Result
import at.searles.parsing.reader.PositionReader

class SelfReferentialParser<A>(private val initializer: (SelfReferentialParser<A>) -> Parser<A>): Parser<A> {
    private var parser: Parser<A>? = null

    override fun parse(reader: PositionReader): Result<A> {
        initialize()
        return parser?.parse(reader) ?: error("SelfReferentialParser is not initialized")
    }

    private fun initialize() {
        if (parser == null) {
            parser = initializer(this)
        }
    }
}

fun <A> self(initializer: (SelfReferentialParser<A>) -> Parser<A>): SelfReferentialParser<A> {
    return SelfReferentialParser(initializer)
}