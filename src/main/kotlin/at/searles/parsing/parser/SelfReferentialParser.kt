package at.searles.parsing.parser

import at.searles.parsing.PrintResult
import at.searles.parsing.ParseResult
import at.searles.parsing.reader.PositionReader

class SelfReferentialParser<A>(private val initializer: (SelfReferentialParser<A>) -> Parser<A>): Parser<A> {
    private var isInitialized: Boolean = false
    private lateinit var parser: Parser<A>

    override fun parse(reader: PositionReader): ParseResult<A> {
        initialize()
        return parser.parse(reader)
    }

    override fun print(value: A): PrintResult {
        initialize()
        return parser.print(value)
    }

    private fun initialize() {
        if (!isInitialized) {
            parser = initializer(this)
            isInitialized = true
        }
    }
}

fun <A> self(initializer: (SelfReferentialParser<A>) -> Parser<A>): SelfReferentialParser<A> {
    return SelfReferentialParser(initializer)
}