package at.searles.parsing.reader

class BufferedIndexedReader(private val delegate: CodePointReader, private val bufSize: Int = 65536): IndexedReader {
    init {
        require(bufSize > 0)
    }

    private val buf = IntArray(bufSize)

    private var count: Long = 0
    override var index: Long = 0
        set(value) {
            if (count - value > bufSize || value > count) {
                throw IllegalArgumentException("value outside of valid range")
            }
            field = value
        }

    override fun read(): Int {
        val codePoint: Int

        if (index < count) {
            codePoint = buf[(index % bufSize).toInt()]
        } else {
            assert(count == index)
            codePoint = delegate.read()
            if (codePoint == -1) return -1
            buf[(count % bufSize).toInt()] = codePoint
            count ++
        }

        index ++
        return codePoint
    }
}