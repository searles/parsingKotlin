package at.searles.parsing.utils

import kotlin.math.max
import kotlin.math.min

class IntRangeSet {
    private val ranges = ArrayList<IntRange>()

    fun add(range: IntRange) {
        // -1 because touch is fine.
        val pos = find(range.first - 1)

        if(pos == ranges.size) {
            ranges.add(range)
            return
        }

        val start = min(range.first, ranges[pos].first)
        var last = range.last

        while(pos < ranges.size && ranges[pos].first - 1 <= last) {
            last = max(last, ranges[pos].last)
            ranges.removeAt(pos)
        }

        ranges.add(pos, (start .. last))
    }

    fun contains(value: Int): Boolean {
        val pos = find(value)
        return pos < ranges.size && value in ranges[pos]
    }

    private fun find(value: Int): Int {
        var l = 0
        var r = ranges.size

        while(l != r) {
            val m = (l + r) / 2

            // is interval left of intervals[m]
            when {
                value < ranges[m].first -> r = m
                ranges[m].last < value -> l = m + 1
                else -> return m
            }
        }

        return l
    }
}