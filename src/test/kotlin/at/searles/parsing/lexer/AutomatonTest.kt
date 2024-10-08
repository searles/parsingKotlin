package at.searles.parsing.lexer

import at.searles.parsing.ParseFailure
import at.searles.parsing.ParseSuccess
import at.searles.parsing.reader.StringCodePointReader
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class AutomatonTest {
    private val eofAutomaton = Automaton.ofRange(listOf(-1 .. -1))

    @Test
    fun testAutomatonFromString() {
        // Arrange
        val automaton = Automaton.ofString("Hello World").applyLabel(Label(0))
        val reader = StringCodePointReader("Hello World!")

        // Act
        val result = automaton.consume(reader)

        // Assert
        Assertions.assertTrue(result is ParseSuccess)
        Assertions.assertEquals('!'.code, reader.read())
    }

    @Test
    fun testAutomatonFromRange() {
        // Arrange
        val automaton = Automaton.ofRange(listOf('A'.code .. 'C'.code, 'G'.code .. 'I'.code, 'B'.code .. 'D'.code)).applyLabel(Label(0))

        // Act / Assert
        Assertions.assertTrue(automaton.consume(StringCodePointReader("0")) is ParseFailure)
        Assertions.assertTrue(automaton.consume(StringCodePointReader("A")) is ParseSuccess)
        Assertions.assertTrue(automaton.consume(StringCodePointReader("B")) is ParseSuccess)
        Assertions.assertTrue(automaton.consume(StringCodePointReader("C")) is ParseSuccess)
        Assertions.assertTrue(automaton.consume(StringCodePointReader("D")) is ParseSuccess)
        Assertions.assertTrue(automaton.consume(StringCodePointReader("E")) is ParseFailure)
        Assertions.assertTrue(automaton.consume(StringCodePointReader("F")) is ParseFailure)
        Assertions.assertTrue(automaton.consume(StringCodePointReader("G")) is ParseSuccess)
        Assertions.assertTrue(automaton.consume(StringCodePointReader("H")) is ParseSuccess)
        Assertions.assertTrue(automaton.consume(StringCodePointReader("I")) is ParseSuccess)
        Assertions.assertTrue(automaton.consume(StringCodePointReader("J")) is ParseFailure)
    }

    @Test
    fun testOr() {
        // Arrange
        val automatonAA = Automaton.ofString("AA")
        val automatonBB = Automaton.ofString("BB")

        val automatonAAorBB = automatonAA.or(automatonBB).applyLabel(Label(0))

        // Act / Assert
        Assertions.assertTrue(automatonAAorBB.consume(StringCodePointReader("AA")) is ParseSuccess)
        Assertions.assertTrue(automatonAAorBB.consume(StringCodePointReader("AB")) is ParseFailure)
        Assertions.assertTrue(automatonAAorBB.consume(StringCodePointReader("BA")) is ParseFailure)
        Assertions.assertTrue(automatonAAorBB.consume(StringCodePointReader("BB")) is ParseSuccess)
    }

    @Test
    fun testThen() {
        // Arrange
        val automatonA = Automaton.ofString("A")
        val automatonB = Automaton.ofString("B")

        val automatonAB = automatonA.then(automatonB).applyLabel(Label(0))

        // Act / Assert
        Assertions.assertTrue(automatonAB.consume(StringCodePointReader("AA")) is ParseFailure)
        Assertions.assertTrue(automatonAB.consume(StringCodePointReader("AB")) is ParseSuccess)
        Assertions.assertTrue(automatonAB.consume(StringCodePointReader("BA")) is ParseFailure)
        Assertions.assertTrue(automatonAB.consume(StringCodePointReader("BB")) is ParseFailure)
    }

    @Test
    fun testRepeat() {
        // Arrange
        val automatonA = Automaton.ofString("A")
        val automatonAs = automatonA.plus().then(eofAutomaton).applyLabel(Label(0))

        // Act / Assert
        Assertions.assertTrue(automatonAs.consume(StringCodePointReader("")) is ParseFailure)
        Assertions.assertTrue(automatonAs.consume(StringCodePointReader("A")) is ParseSuccess)
        Assertions.assertTrue(automatonAs.consume(StringCodePointReader("AAAA")) is ParseSuccess)
        Assertions.assertTrue(automatonAs.consume(StringCodePointReader("B")) is ParseFailure)
    }

    @Test
    fun testOptional() {
        // Arrange
        val automatonA = Automaton.ofString("A")
        val automatonAopt = automatonA.opt().then(eofAutomaton).applyLabel(Label(0))

        // Act / Assert
        Assertions.assertTrue(automatonAopt.consume(StringCodePointReader("")) is ParseSuccess)
        Assertions.assertTrue(automatonAopt.consume(StringCodePointReader("A")) is ParseSuccess)
        Assertions.assertTrue(automatonAopt.consume(StringCodePointReader("B")) is ParseFailure)
    }
}