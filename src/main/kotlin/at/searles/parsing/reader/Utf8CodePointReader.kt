package at.searles.parsing.reader

import java.io.InputStream

class Utf8CodePointReader(private val inputStream: InputStream) : CodePointReader {
    private val buf = ByteArray(4)

    override fun read(): Int {
        val ch = inputStream.read()
        if (ch == -1) return -1
        buf[0] = ch.toByte()

        val byteCount = getByteCount(ch)

        for (i in 1 until byteCount) {
            val nextByte = inputStream.read()
            if (nextByte == -1) throw IllegalArgumentException("Unexpected end of stream")
            if (nextByte and 0b11000000 != 0b10000000) throw IllegalArgumentException("Invalid UTF-8 continuation byte: $nextByte")
            buf[i] = nextByte.toByte()
        }

        return String(buf, 0, byteCount, Charsets.UTF_8).codePointAt(0)
    }

    private fun getByteCount(firstByte: Int): Int {
        return when {
            firstByte and 0b10000000 == 0 -> 1 // 1-byte character (ASCII)
            firstByte and 0b11100000 == 0b11000000 -> 2 // 2-byte character
            firstByte and 0b11110000 == 0b11100000 -> 3 // 3-byte character
            firstByte and 0b11111000 == 0b11110000 -> 4 // 4-byte character
            else -> throw IllegalArgumentException("Invalid UTF-8 start byte: $firstByte")
        }
    }
}
