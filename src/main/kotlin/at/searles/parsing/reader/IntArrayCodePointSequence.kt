package at.searles.parsing.reader

class IntArrayCodePointSequence(private vararg val codePoint: Int, private val indices: IntRange = codePoint.indices): CodePointSequence {
    override fun get(index: Int): Int {
        val offset = index + indices.first
        return if (offset in indices) codePoint[offset] else -1
    }

    override fun length(): Int {
        return indices.last - indices.first + 1
    }

    override fun toString(): String {
        val sb = StringBuilder()
        indices.map { codePoint[it] }.filter { it != -1 }.forEach {
            sb.appendCodePoint(it)
        }

        return sb.toString()
    }

    override fun toReader(): PositionReader {
        return CPReader()
    }

    private inner class CPReader: PositionReader {
        override var position: Long = indices.first.toLong()

        override fun getSequence(start: Long, end: Long): CodePointSequence {
            TODO()
        }

        override fun read(): Int {
            return if (position !in indices) {
                -1
            } else {
                position ++
                codePoint[(position - 1).toInt()]
            }
        }
    }
}