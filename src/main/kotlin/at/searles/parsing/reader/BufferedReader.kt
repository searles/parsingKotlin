package at.searles.parsing.reader

import java.lang.StringBuilder

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

    override fun getSequence(start: Long, end: Long): CodePointSequence {
        return InternalSubSequence(start, end)
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

    private inner class InternalSubSequence(val start: Long, val end: Long) : CodePointSequence {
        override fun get(index: Int): Int {
            if (0 > index ||  end - start <= index) {
                return -1
            } else {
                require(count - bufSize <= index + start)
                return buf[((index + start).toInt() % bufSize)]
            }
        }

        override fun length(): Int {
            return (end - start).toInt()
        }

        override fun toString(): String {
            val sb = StringBuilder()

            var index = 0
            var cp = this[index]

            while(cp != -1) {
                sb.appendCodePoint(cp)
                cp = this[++index]
            }

            return sb.toString()
        }

        override fun toReader(): PositionReader {
            return InternalSubReader(start, end)
        }
    }

    private inner class InternalSubReader(val start: Long, val end: Long) : PositionReader {
        override var position: Long = start
            set(value) {
                require(value in start..end)
                field = value
            }

        override fun getSequence(start: Long, end: Long): CodePointSequence {
            return InternalSubSequence(start + this.start, end + this.start)
        }

        override fun read(): Int {
            if (position >= end) {
                return -1
            } else {
                require(count - bufSize <= position)
                return buf[(position++ % bufSize).toInt()]
            }
        }
    }
}