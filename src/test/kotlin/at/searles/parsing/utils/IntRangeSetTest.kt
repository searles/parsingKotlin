package at.searles.parsing.utils

import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class IntRangeSetTest {
    @Test
    fun testAddNonOverlappingRange() {
        val rangeSet = IntRangeSet()
        rangeSet.add(1..5)
        assertTrue(rangeSet.contains(1))
        assertTrue(rangeSet.contains(5))
        assertFalse(rangeSet.contains(6))
    }

    @Test
    fun testAddOverlappingRange() {
        val rangeSet = IntRangeSet()
        rangeSet.add(1..5)
        rangeSet.add(4..10)
        assertTrue(rangeSet.contains(3))
        assertTrue(rangeSet.contains(4))
        assertTrue(rangeSet.contains(5))
        assertTrue(rangeSet.contains(6))
        assertTrue(rangeSet.contains(10))
        assertFalse(rangeSet.contains(11))
    }

    @Test
    fun testAddTouchingRange() {
        val rangeSet = IntRangeSet()
        rangeSet.add(1..5)
        rangeSet.add(6..10)
        assertTrue(rangeSet.contains(5))
        assertTrue(rangeSet.contains(6))
        assertTrue(rangeSet.contains(10))
        assertFalse(rangeSet.contains(11))
    }

    @Test
    fun testContainsWithEmptySet() {
        val rangeSet = IntRangeSet()
        assertFalse(rangeSet.contains(1))
    }

    @Test
    fun testAddMultipleRangesAndCheckContainment() {
        val rangeSet = IntRangeSet()
        rangeSet.add(1..3)
        rangeSet.add(6..8)
        rangeSet.add(10..12)

        assertTrue(rangeSet.contains(1))
        assertTrue(rangeSet.contains(2))
        assertTrue(rangeSet.contains(3))
        assertFalse(rangeSet.contains(4))
        assertFalse(rangeSet.contains(5))
        assertTrue(rangeSet.contains(6))
        assertTrue(rangeSet.contains(7))
        assertTrue(rangeSet.contains(8))
        assertFalse(rangeSet.contains(9))
        assertTrue(rangeSet.contains(10))
        assertTrue(rangeSet.contains(11))
        assertTrue(rangeSet.contains(12))
        assertFalse(rangeSet.contains(13))
    }

    @Test
    fun testMergeRanges() {
        val rangeSet = IntRangeSet()
        rangeSet.add(1..3)
        rangeSet.add(5..7)
        rangeSet.add(2..6) // This should merge all previous ranges into 1..7

        assertTrue(rangeSet.contains(1))
        assertTrue(rangeSet.contains(2))
        assertTrue(rangeSet.contains(3))
        assertTrue(rangeSet.contains(4))
        assertTrue(rangeSet.contains(5))
        assertTrue(rangeSet.contains(6))
        assertTrue(rangeSet.contains(7))
        assertFalse(rangeSet.contains(8))
    }
}
