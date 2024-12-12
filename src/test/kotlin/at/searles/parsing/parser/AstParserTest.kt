package at.searles.parsing.parser

import at.searles.parsing.ParseFailure
import at.searles.parsing.ParseSuccess
import at.searles.parsing.parser.arithmetics.AstParser
import at.searles.parsing.reader.StringCodePointReader
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource

class AstParserTest {
    @ParameterizedTest
    @CsvSource(
        "((2)), 2",
        "-1, (- 1)",
        "-((1)), (- 1)",
        "1+2+3+4, (+ (+ (+ 1 2) 3) 4)",
        "3-(2+1), (- 3 (+ 2 1))",
        "1+2+3*4, (+ (+ 1 2) (* 3 4))",
        "1--2, (- 1 (- 2))"
    )
    fun `WHEN given a arithmetic sum THEN the correct result is determined`(expr: String, expected: String) {
        // Arrange
        val reader = StringCodePointReader(expr)

        // Act
        val result = AstParser.expr.parse(Unit, reader)

        // Assert
        Assertions.assertEquals(-1, reader.read())
        Assertions.assertTrue(result is ParseSuccess)
        Assertions.assertEquals(expected, (result as ParseSuccess).value.toString())
    }

    @ParameterizedTest
    @CsvSource(
        "((2)), 2",
        "-1, -1",
        "-((1)), -1",
        "(1+2+3)+4, 1+2+3+4",
        "3-(2+1), 3-(2+1)",
        "1+2+(3*4), 1+2+3*4",
        "2-(-3), 2--3"
    )    fun `WHEN parsing and printing a tree THEN the printed ast is correct`(expr: String, expected: String) {
        // Arrange
        val reader = StringCodePointReader(expr)

        // Act
        val result = when (val ast = AstParser.expr.parse(Unit, reader)) {
            is ParseFailure -> Assertions.fail()
            is ParseSuccess -> AstParser.expr.print(ast.value)
        }

        // Assert
        Assertions.assertTrue(result is PrintSuccess)
        Assertions.assertEquals(expected, (result as PrintSuccess).toString())
    }
}