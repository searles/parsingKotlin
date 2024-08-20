package at.searles.parsing.reader

import org.junit.jupiter.api.Assertions
import java.io.ByteArrayInputStream
import kotlin.test.Test

class Utf8CodePointReaderTest {
    @Test
    fun `WHEN reading a bad first byte THEN exception is thrown`() {
        // Arrange
        val reader = Utf8CodePointReader(ByteArrayInputStream(byteArrayOf(0b10111111.toByte())))

        // Act
        try {
            reader.read()
            // Assert
            Assertions.fail("")
        } catch (_: Exception) {}
    }
}