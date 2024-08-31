package at.searles.parsing.reader

import at.searles.parsing.ParseResult

interface Consumer<A> {
    fun consume(reader: PositionReader): ParseResult<A>
}
