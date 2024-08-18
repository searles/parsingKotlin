package at.searles.parsing.reader

class StringCodePointReader(private val string: String) : PositionReader {
    override var position: Long = 0

    override fun getReader(start: Long, end: Long): PositionReader {
        return StringCodePointReader(string.substring(start.toInt(), end.toInt()))
    }

    override fun read(): Int {
        if (position < string.length) {
            val codePoint = string.codePointAt(position.toInt())
            position += Character.charCount(codePoint)
            return codePoint
        } else {
            return -1
        }
    }
}