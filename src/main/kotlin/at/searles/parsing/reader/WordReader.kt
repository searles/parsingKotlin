package at.searles.parsing.reader

class WordReader(val delegate: IndexedReader) : CodePointReader by delegate {
    var startIndex = delegate.index
        private set
    var endIndex = -1L
        private set

    fun drop() {
        endIndex = -1
        delegate.index = startIndex
    }

    fun mark() {
        endIndex = delegate.index
    }

    fun accept() {
        startIndex = endIndex
        endIndex = -1
        delegate.index = startIndex
    }
}