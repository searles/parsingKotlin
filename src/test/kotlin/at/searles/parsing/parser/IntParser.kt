package at.searles.parsing.parser

import at.searles.parsing.reader.IndexedReader

class IntParser : Parser<Int> {
    override fun parse(reader: IndexedReader): ParserResult<Int> {
        val startIndex = reader.index
        var endIndex = -1L

        var n = 0
        while (true) {
            when (val ch = reader.read()) {
                in '0'.code.. '9'.code -> {
                    endIndex = reader.index
                    n = 10 * n + ch - '0'.code
                }
                else -> break
            }
        }

        if (endIndex == -1L) {
            reader.index = startIndex
            return ParserResult.failure()
        } else {
            reader.index = endIndex
            return ParserResult.success(n)
        }
    }
}