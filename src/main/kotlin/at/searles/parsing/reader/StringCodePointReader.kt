package at.searles.parsing.reader

import at.searles.parsing.reader.CodePointSequence.Companion.asCodePointSequence

class StringCodePointReader(private val string: String) : PositionReader {
    override var position: Long = 0

    override fun getSequence(start: Long, end: Long): CodePointSequence {
        return string.substring(start.toInt(), end.toInt()).asCodePointSequence()
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