package at.searles.parsing.reader

class BufferedReader(private val delegate: CodePointReader, private val bufSize: Int = 65536): PositionReader {
    init {
        require(bufSize > 0)
    }

    private val buf = IntArray(bufSize)

    /**
     * number of codepoints read from delegate
     */
    private var count: Long = 0

    /**
     * Current index
     */
    override var position: Long = 0
        set(value) {
            require (value in count - bufSize .. count )
            field = value
        }

    override fun getReader(start: Long, end: Long): PositionReader {
        return SubReader(start, end)
    }

    override fun read(): Int {
        val codePoint: Int

        if (position < count) {
            codePoint = buf[(position % bufSize).toInt()]
        } else {
            assert(count == position)
            codePoint = delegate.read()
            if (codePoint == -1) return -1
            buf[(count % bufSize).toInt()] = codePoint
            count ++
        }

        position ++
        return codePoint
    }

    private inner class SubReader(val start: Long, val end: Long) : PositionReader {
        override var position: Long = start
            set(value) {
                require(value in start..end)
                field = value
            }

        override fun getReader(start: Long, end: Long): PositionReader {
            return SubReader(start, end)
        }

        override fun read(): Int {
            if (position >= end) {
                return -1
            } else {
                require(count - bufSize < position)
                return buf[(position % bufSize).toInt()]
            }
        }
    }
}