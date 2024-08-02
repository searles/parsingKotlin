package at.searles.parsing.parser

import at.searles.parsing.reader.StringCodePointReader
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource

class ArithmeticParserTest {
    private val rule = IntParser() + (
        KeywordRecognizer("+") + IntParser() + Fold { left: Int, right: Int -> left + right } or
        KeywordRecognizer("-") + IntParser() + Fold { left: Int, right: Int -> left - right }
    )

    @ParameterizedTest
    @CsvSource(
        "12+15, 27",
        "5+7, 12",
        "100+200, 300",
        "23-11, 12",
        "100-1, 99"
    )    fun `WHEN given a simple arithmetic sum THEN the correct result is determined`(expr: String, expected: Int) {
        // Act
        val result = rule.parse(StringCodePointReader(expr))

        // Assert
        Assertions.assertTrue(result.isSuccess)
        Assertions.assertEquals(expected, result.value)
    }
}