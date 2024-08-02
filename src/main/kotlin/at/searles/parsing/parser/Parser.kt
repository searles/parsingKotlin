package at.searles.parsing.parser

import at.searles.parsing.reader.IndexedReader

interface Parser<A> {
    fun parse(reader: IndexedReader): ParserResult<A>

}