package at.searles.parsing.reader

import at.searles.parsing.PrintResult
import at.searles.parsing.ParseResult

interface Consumer<A> {
    fun consume(reader: PositionReader): ParseResult<A>
    fun print(label: A, sequence: CodePointSequence): PrintResult
    fun print(label: A): PrintResult
}
