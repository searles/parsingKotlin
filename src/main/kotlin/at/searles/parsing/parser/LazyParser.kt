package at.searles.parsing.parser

import at.searles.parsing.Result
import at.searles.parsing.reader.PositionReader

class LazyParser<A>(private val init: (LazyParser<A>) -> Parser<A>): Parser<A> {
    private val parser: Parser<A> by lazy { init(this) }

    override fun parse(reader: PositionReader): Result<A> {
        return parser.parse(reader)
    }
}