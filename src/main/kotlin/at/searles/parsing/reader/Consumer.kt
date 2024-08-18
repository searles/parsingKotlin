package at.searles.parsing.reader

import at.searles.parsing.Result

interface Consumer<A> {
    fun consume(reader: PositionReader): Result<A>
}
