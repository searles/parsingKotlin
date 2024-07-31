package at.searles.parsing.reader

class StringCodePointReader(val string: String) : IndexedReader {
    override var index: Long = 0

    override fun read(): Int {
        if (index < string.length) {
            val codePoint = string.codePointAt(index.toInt())
            index += Character.charCount(codePoint)
            return codePoint
        } else {
            return -1
        }
    }
}