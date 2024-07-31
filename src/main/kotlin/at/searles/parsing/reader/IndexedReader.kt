package at.searles.parsing.reader

interface IndexedReader : CodePointReader {
    var index: Long
}