package at.searles.parsing.utils

import kotlin.math.min

class IntRangeMap<A> {
    private val entries = ArrayList<Entry<A>>()
    val values get() = entries.map { it.value }

    fun add(other: IntRangeMap<A>, mergeFn: (A, A) -> A = { _, _ -> error("Intersection not supported") }) {
        other.entries.forEach { add(it.range, it.value, mergeFn) }
    }

    fun add(range: IntRange, value: A, mergeFn: (A, A) -> A = { _, _ -> error("Intersection not supported") }) {
        // Basic binary search
        var pos = find(range.first)

        var start = range.first
        val end = range.last + 1

        while(start < end) {
            if(pos == entries.size) {
                entries.add(Entry((start until end), value))
                return
            }

            val entry = entries[pos]

            if (start < entry.range.first) {
                val nextEnd = min(end, entry.range.first)
                entries.add(pos, Entry((start until nextEnd), value))

                start = nextEnd
                pos++
            } else if (start == entry.range.first) {
                if (end < entry.range.last + 1) {
                    entries.add(pos, Entry((start until end), mergeFn(entry.value, value)))
                    entries[pos + 1] = Entry((end .. entry.range.last), entry.value)
                    start = end
                } else {
                    entries[pos] = Entry((start .. entry.range.last), mergeFn(entry.value, value))
                    start = entry.range.last + 1
                    pos++
                }
            } else {
                require(start < entry.range.last + 1) { "bug in binary search: $start, ${entry.range}" }
                entries.add(pos, Entry((entry.range.first until start), entry.value))
                entries[pos + 1] = Entry((start .. entry.range.last), entry.value)
                pos++
            }
        }
    }

    operator fun get(value: Int): A? {
        val pos = find(value)

        return entries.getOrNull(pos)?.let {
            return if(value in it.range) {
                it.value
            } else {
                null
            }
        }
    }

    override fun toString(): String {
        return entries.joinToString(", ")
    }

    private fun addAll(newEntries: List<Entry<A>>) {
        entries.addAll(newEntries)
    }

    fun <B> mapValues(conversion: (A) -> B): IntRangeMap<B> {
        val newEntries = entries.map { Entry(it.range, conversion(it.value)) }

        return IntRangeMap<B>().apply {
            addAll(newEntries)
        }
    }

    private fun find(value: Int): Int {
        var l = 0
        var r = entries.size

        while(l != r) {
            val m = (l + r) / 2

            when {
                value < entries[m].range.first -> r = m
                entries[m].range.last + 1 <= value -> l = m + 1
                else -> return m
            }
        }

        return l
    }

    class Entry<A>(val range: IntRange, val value: A) {
        override fun toString(): String {
            return "$range -> $value"
        }
    }
}
