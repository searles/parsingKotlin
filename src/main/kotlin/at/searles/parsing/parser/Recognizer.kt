package at.searles.parsing.parser

import at.searles.parsing.reader.IndexedReader

interface Recognizer {
    fun recognize(reader: IndexedReader): Boolean
}