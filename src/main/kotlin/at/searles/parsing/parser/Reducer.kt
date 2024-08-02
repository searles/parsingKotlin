package at.searles.parsing.parser

import at.searles.parsing.reader.IndexedReader

interface Reducer<L, R> {
    fun parse(reader: IndexedReader, input: L): ParserResult<R>
}