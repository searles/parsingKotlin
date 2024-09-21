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
        var index = find(range.first)

        // The range that we want to add:
        var position = range.first
        val last = range.last

        while(position <= last) {
            if(index == entries.size) {
                entries.add(Entry(position .. last, value))
                return
            }

            val entry = entries[index]

            if (position < entry.range.first) {
                val nextLast = min(last, entry.range.first - 1)
                entries.add(index, Entry(position .. nextLast, value))
                index ++
                position = nextLast + 1
            } else if (position == entry.range.first) {
                val nextLast = min(last, entry.range.last)
                val newEntry = Entry(position .. nextLast, mergeFn(entry.value, value))
                position = nextLast + 1

                if (position <= entry.range.last) {
                    entries.add(index, newEntry)
                    entries[index + 1] = Entry(position .. entry.range.last, entry.value)
                } else {
                    entries[index] = newEntry
                }

                index ++
            } else {
                entries.add(index, Entry(entry.range.first..< position, entry.value))
                entries[index + 1] = Entry((position .. entry.range.last), entry.value)
                index ++
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
                entries[m].range.last < value -> l = m + 1
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
