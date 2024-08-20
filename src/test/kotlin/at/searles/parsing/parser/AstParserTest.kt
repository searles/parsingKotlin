package at.searles.parsing.parser

import at.searles.parsing.Success
import at.searles.parsing.parser.arithmetics.AstParser
import at.searles.parsing.parser.arithmetics.MathParser
import at.searles.parsing.reader.StringCodePointReader
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource

class AstParserTest {
    @ParameterizedTest
    @CsvSource(
        "((2)), 2",
        "-1, (~ 1)",
        "-((1)), (~ 1)",
        "1+2+3+4, (+ (+ (+ 1 2) 3) 4)",
        "3-(2+1), (- 3 (+ 2 1))",
        "1+2+3*4, (+ (+ 1 2) (* 3 4))",
    )    fun `WHEN given a arithmetic sum THEN the correct result is determined`(expr: String, expected: String) {
        // Arrange
        val reader = StringCodePointReader(expr)

        // Act
        val result = AstParser.expr.parse(reader)

        // Assert
        Assertions.assertEquals(-1, reader.read())
        Assertions.assertTrue(result is Success)
        Assertions.assertEquals(expected, (result as Success).value.toString())
    }
}