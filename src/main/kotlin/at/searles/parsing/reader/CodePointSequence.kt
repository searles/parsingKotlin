package at.searles.parsing.reader

/**
 * This is very similar to CharSequence with one exception: Get might
 * not return a valid code point. If it does not, it returns -1.
 * These gaps are on purpose so that no complex conversion of indices
 * from UTF-8 ByteArrays, Java UTF-16 Strings or other things are needed.
 * When out of range, also -1 is returned. This will never throw an
 * out-of-bounds exception.
 */
interface CodePointSequence {
    /**
     * Returns -1 if index is invalid. This not necessarily means that
     * it is out of range (to allow interoperability with CharSequence)
     */
    operator fun get(index: Int): Int

    /**
     * Returns the length.
     */
    fun length(): Int

    override fun toString(): String
    fun toReader(): PositionReader

    companion object {
        fun fromCodePoint(vararg codepoint: Int): CodePointSequence {
            return IntArrayCodePointSequence(*codepoint)
        }

        fun CharSequence.asCodePointSequence(): CodePointSequence {
            val charSequence = this

            return object: CodePointSequence {
                override fun get(index: Int): Int {
                    if (index !in charSequence.indices) {
                        return -1
                    }

                    if (Character.isLowSurrogate(charSequence[index])) {
                        return -1
                    }

                    return Character.codePointAt(charSequence, index)
                }

                override fun length(): Int {
                    return charSequence.length
                }

                override fun toString(): String {
                    return charSequence.toString()
                }

                override fun toReader(): PositionReader {
                    return StringCodePointReader(charSequence.toString())
                }
            }
        }
    }
}