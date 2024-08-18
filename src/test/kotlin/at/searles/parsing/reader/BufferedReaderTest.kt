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
        val indexedReader = BufferedReader(codePointReader, bufSize = 4)

        // Act
        var str = ""
        repeat(testString.length) {
            str += indexedReader.read().toChar()
        }

        // Assert
        Assertions.assertEquals(testString, str)
        Assertions.assertEquals(testString.length.toLong(), indexedReader.position)
        Assertions.assertEquals(-1, indexedReader.read())
        Assertions.assertEquals(testString.length.toLong(), indexedReader.position, "Reading after end does not change index")
    }

    @Test
    fun `WHEN changing index to lower value THEN char at index is read from buffer`() {
        // Arrange
        val testString = "Hello World"
        val codePointReader = Utf8CodePointReader(ByteArrayInputStream(testString.toByteArray(Charsets.UTF_8)))
        val indexedReader = BufferedReader(codePointReader, bufSize = 4)

        // Act
        repeat(10) {
            indexedReader.read()
        }

        indexedReader.position = 6

        // Assert
        Assertions.assertEquals('W', indexedReader.read().toChar())
        Assertions.assertEquals('o', indexedReader.read().toChar())
        Assertions.assertEquals('r', indexedReader.read().toChar())
        Assertions.assertEquals('l', indexedReader.read().toChar())
        Assertions.assertEquals('d', indexedReader.read().toChar())
        Assertions.assertEquals(-1, indexedReader.read())
    }

    @Test
    fun `WHEN changing index to higher value THEN char at index is read`() {
        // Arrange
        val testString = "Hello World"
        val codePointReader = Utf8CodePointReader(ByteArrayInputStream(testString.toByteArray(Charsets.UTF_8)))
        val indexedReader = BufferedReader(codePointReader, bufSize = 4)

        // Act
        repeat(10) { indexedReader.read() }
        indexedReader.position = 6

        // Assert
        Assertions.assertEquals('W', indexedReader.read().toChar())
        Assertions.assertEquals('o', indexedReader.read().toChar())
        Assertions.assertEquals('r', indexedReader.read().toChar())
        Assertions.assertEquals('l', indexedReader.read().toChar())
        Assertions.assertEquals('d', indexedReader.read().toChar())
        Assertions.assertEquals(-1, indexedReader.read())
    }

    @Test
    fun `WHEN changing index to lower value exceeding buf size THEN exception is thrown`() {
        // Arrange
        val testString = "Hello World"
        val codePointReader = Utf8CodePointReader(ByteArrayInputStream(testString.toByteArray(Charsets.UTF_8)))
        val indexedReader = BufferedReader(codePointReader, bufSize = 4)

        // Act
        repeat(10) { indexedReader.read() }

        // Assert
        try {
            indexedReader.position = 5
            Assertions.fail()
        } catch (_: IllegalArgumentException) {}
    }
}