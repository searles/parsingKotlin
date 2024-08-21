package at.searles.parsing.reader

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.io.ByteArrayInputStream

class BufferedReaderTest {
    @Test
    fun `WHEN reading string THEN string is read and indices are correct`() {
        // Arrange
        val testString = "Hello World"
        val codePointReader = Utf8CodePointReader(ByteArrayInputStream(testString.toByteArray(Charsets.UTF_8)))
        val reader = BufferedReader(codePointReader, bufSize = 4)

        // Act
        var str = ""
        repeat(testString.length) {
            str += reader.read().toChar()
        }

        // Assert
        Assertions.assertEquals(testString, str)
        Assertions.assertEquals(testString.length.toLong(), reader.position)
        Assertions.assertEquals(-1, reader.read())
        Assertions.assertEquals(testString.length.toLong(), reader.position, "Reading after end does not change index")
    }

    @Test
    fun `WHEN changing index to lower value THEN char at index is read from buffer`() {
        // Arrange
        val testString = "Hello World"
        val codePointReader = Utf8CodePointReader(ByteArrayInputStream(testString.toByteArray(Charsets.UTF_8)))
        val reader = BufferedReader(codePointReader, bufSize = 4)

        // Act
        repeat(10) {
            reader.read()
        }

        reader.position = 6

        // Assert
        Assertions.assertEquals('W', reader.read().toChar())
        Assertions.assertEquals('o', reader.read().toChar())
        Assertions.assertEquals('r', reader.read().toChar())
        Assertions.assertEquals('l', reader.read().toChar())
        Assertions.assertEquals('d', reader.read().toChar())
        Assertions.assertEquals(-1, reader.read())
    }

    @Test
    fun `WHEN changing index to higher value THEN char at index is read`() {
        // Arrange
        val testString = "Hello World"
        val codePointReader = Utf8CodePointReader(ByteArrayInputStream(testString.toByteArray(Charsets.UTF_8)))
        val reader = BufferedReader(codePointReader, bufSize = 4)

        // Act
        repeat(10) { reader.read() }
        reader.position = 6

        // Assert
        Assertions.assertEquals('W', reader.read().toChar())
        Assertions.assertEquals('o', reader.read().toChar())
        Assertions.assertEquals('r', reader.read().toChar())
        Assertions.assertEquals('l', reader.read().toChar())
        Assertions.assertEquals('d', reader.read().toChar())
        Assertions.assertEquals(-1, reader.read())
    }

    @Test
    fun `WHEN changing index to lower value exceeding buf size THEN exception is thrown`() {
        // Arrange
        val testString = "Hello World"
        val codePointReader = Utf8CodePointReader(ByteArrayInputStream(testString.toByteArray(Charsets.UTF_8)))
        val reader = BufferedReader(codePointReader, bufSize = 4)

        // Act
        repeat(10) { reader.read() }

        // Assert
        try {
            reader.position = 5
            Assertions.fail()
        } catch (_: IllegalArgumentException) {}
    }

    @Test
    fun `WHEN creating subsequence THEN char contains correct chars`() {
        // Arrange
        val testString = "Hello World"
        val codePointReader = Utf8CodePointReader(ByteArrayInputStream(testString.toByteArray(Charsets.UTF_8)))
        val reader = BufferedReader(codePointReader, bufSize = 4)

        // Act
        repeat(5) { reader.read() }
        val sub = reader.getSequence(1L, 5L)

        // Assert
        Assertions.assertEquals('e'.code, sub[0])
        Assertions.assertEquals('l'.code, sub[1])
        Assertions.assertEquals('l'.code, sub[2])
        Assertions.assertEquals('o'.code, sub[3])
        Assertions.assertEquals(-1, sub[-1])
        Assertions.assertEquals(-1, sub[4])
    }


    @Test
    fun `WHEN creating subreader of subreader THEN char contains correct chars`() {
        // Arrange
        val testString = "Hello World"
        val codePointReader = Utf8CodePointReader(ByteArrayInputStream(testString.toByteArray(Charsets.UTF_8)))
        val reader = BufferedReader(codePointReader, bufSize = 4)

        // Act
        repeat(5) { reader.read() }
        val sub = reader.getSequence(1L, 5L).toReader().getSequence(0L, 2L).toReader()

        // Assert
        Assertions.assertEquals('e'.code, sub.read())
        Assertions.assertEquals('l'.code, sub.read())
        Assertions.assertEquals(-1, sub.read())
    }

    @Test
    fun `WHEN creating subreader AND reading from non-existing position THEN exception is thrown`() {
        // Arrange
        val testString = "Hello World"
        val codePointReader = Utf8CodePointReader(ByteArrayInputStream(testString.toByteArray(Charsets.UTF_8)))
        val reader = BufferedReader(codePointReader, bufSize = 4)

        // Act
        repeat(5) { reader.read() }
        val sub = reader.getSequence(0L, 5L) // 0 is out of bounds

        // Assert
        try {
            sub[0]
            Assertions.fail()
        } catch (_: Exception) {}
    }

    @Test
    fun `WHEN reading from subreader AND advancing in reader THEN exception is thrown`() {
        // Arrange
        val testString = "Hello World"
        val codePointReader = Utf8CodePointReader(ByteArrayInputStream(testString.toByteArray(Charsets.UTF_8)))
        val reader = BufferedReader(codePointReader, bufSize = 4)

        // Act
        repeat(5) { reader.read() }
        val sub = reader.getSequence(1L, 5L).toReader()
        reader.read()

        // Assert
        try {
            sub.read()
            Assertions.fail()
        } catch (_: Exception) {}
    }
}
