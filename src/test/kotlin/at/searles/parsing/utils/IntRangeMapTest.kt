package at.searles.parsing.utils

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class IntRangeMapTest {
    @Test
    fun testAddNonOverlappingInterval() {
        val rangeMap = IntRangeMap<String>()
        rangeMap.add(1..5, "A")

        assertTrue(rangeMap.values.contains("A"))
    }

    @Test
    fun testAddOverlappingInterval() {
        val rangeMap = IntRangeMap<String>()
        rangeMap.add(1..5, "A")
        rangeMap.add(4..10, "B") { a, b -> "$a$b" }

        assertEquals("A", rangeMap[3])
        assertEquals("B", rangeMap[6])
        assertEquals("AB", rangeMap[4])
        assertEquals("AB", rangeMap[5])
        assertEquals("B", rangeMap[6])
        assertNull(rangeMap[11])
    }

    @Test
    fun testAddLargeOverlappingInterval() {
        val rangeMap = IntRangeMap<String>()
        rangeMap.add(1..2, "A")
        rangeMap.add(4..5, "B")
        rangeMap.add(7..8, "C")
        rangeMap.add(0..9, "D") { a, b -> "$a$b" }

        assertEquals("D", rangeMap[0])
        assertEquals("AD", rangeMap[1])
        assertEquals("AD", rangeMap[2])
        assertEquals("D", rangeMap[3])
        assertEquals("BD", rangeMap[4])
        assertEquals("BD", rangeMap[5])
        assertEquals("D", rangeMap[6])
        assertEquals("CD", rangeMap[7])
        assertEquals("CD", rangeMap[8])
        assertEquals("D", rangeMap[9])
        assertNull(rangeMap[10])
    }

    @Test
    fun testAddTouchingInterval() {
        val rangeMap = IntRangeMap<String>()
        rangeMap.add(1..5, "A")
        rangeMap.add(6..10, "B")

        assertEquals("A", rangeMap[5])
        assertEquals("B", rangeMap[6])
    }

    @Test
    fun testAddIntervalWithCustomMergeFunction() {
        val rangeMap = IntRangeMap<Int>()
        rangeMap.add(1..5, 10) // Adds 1..5 -> 10
        rangeMap.add(3..7, 20) { a, b -> a + b } // Overlaps and merges with custom function

        assertEquals(10, rangeMap[2]) // Before overlap
        assertEquals(30, rangeMap[4]) // Merged value (10 + 20)
        assertEquals(20, rangeMap[6]) // After overlap
    }

    @Test
    fun testGetValuesInRange() {
        val rangeMap = IntRangeMap<String>()
        rangeMap.add(1..5, "A")
        rangeMap.add(10..15, "B")

        assertEquals("A", rangeMap[1])
        assertEquals("A", rangeMap[3])
        assertEquals("B", rangeMap[12])
        assertNull(rangeMap[6])  // Outside any range
        assertNull(rangeMap[9])  // Between ranges
        assertNull(rangeMap[16]) // After all ranges
    }

    @Test
    fun testAddMultipleIntervals() {
        val rangeMap = IntRangeMap<String>()
        rangeMap.add(1..2, "A")
        rangeMap.add(3..4, "B")
        rangeMap.add(5..6, "C")
        rangeMap.add(7..8, "D")

        assertEquals("A", rangeMap[1])
        assertEquals("B", rangeMap[3])
        assertEquals("C", rangeMap[5])
        assertEquals("D", rangeMap[7])
        assertNull(rangeMap[9])
    }
}
