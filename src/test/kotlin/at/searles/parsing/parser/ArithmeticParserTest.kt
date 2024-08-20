package at.searles.parsing.parser

import at.searles.parsing.Success
import at.searles.parsing.parser.arithmetics.ArithmeticParser
import at.searles.parsing.reader.StringCodePointReader
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource

class ArithmeticParserTest {
    @ParameterizedTest
    @CsvSource(
        "12+15, 27",
        "5+7, 12",
        "100+200, 300",
        "23-11, 12",
        "100-1, 99"
    )    fun `WHEN given a simple arithmetic sum THEN the correct result is determined`(expr: String, expected: Int) {
        // Act
        val result = ArithmeticParser.simpleArithmetic.parse(StringCodePointReader(expr))

        // Assert
        Assertions.assertTrue(result is Success)
        Assertions.assertEquals(expected, (result as Success).value)
    }

    @ParameterizedTest
    @CsvSource(
        "((2)), 2",
        "-1, -1",
        "-((1)), -1",
        "--1, 1",
        "1*2+3, 5",
        "3-(2+1), 0",
        "1+2+3*4, 15",
    )    fun `WHEN given a arithmetic sum THEN the correct result is determined`(expr: String, expected: Int) {
        // Arrange
        val reader = StringCodePointReader(expr)

        // Act
        val result = ArithmeticParser.expr.parse(reader)

        // Assert
        Assertions.assertEquals(-1, reader.read())
        Assertions.assertTrue(result is Success)
        Assertions.assertEquals(expected, (result as Success).value)
    }
}