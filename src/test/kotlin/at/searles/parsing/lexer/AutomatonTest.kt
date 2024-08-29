package at.searles.parsing.lexer

import at.searles.parsing.ParseFailure
import at.searles.parsing.ParseSuccess
import at.searles.parsing.reader.StringCodePointReader
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class AutomatonTest {
    private val eofAutomaton = Automaton.ofRange<Nothing>(-1 .. -1)

    @Test
    fun testAutomatonFromString() {
        // Arrange
        val automaton = Automaton.ofString<Nothing>("Hello World")
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
        val automaton = Automaton.ofRange<Nothing>('A'.code .. 'C'.code, 'G'.code .. 'I'.code, 'B'.code .. 'D'.code)

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
        val automatonAA = Automaton.ofString<Nothing>("AA")
        val automatonBB = Automaton.ofString<Nothing>("BB")

        val automatonAAorBB = automatonAA.or(automatonBB)

        // Act / Assert
        Assertions.assertTrue(automatonAAorBB.consume(StringCodePointReader("AA")) is ParseSuccess)
        Assertions.assertTrue(automatonAAorBB.consume(StringCodePointReader("AB")) is ParseFailure)
        Assertions.assertTrue(automatonAAorBB.consume(StringCodePointReader("BA")) is ParseFailure)
        Assertions.assertTrue(automatonAAorBB.consume(StringCodePointReader("BB")) is ParseSuccess)
    }

    @Test
    fun testThen() {
        // Arrange
        val automatonA = Automaton.ofString<Nothing>("A")
        val automatonB = Automaton.ofString<Nothing>("B")

        val automatonAB = automatonA.then(automatonB)

        // Act / Assert
        Assertions.assertTrue(automatonAB.consume(StringCodePointReader("AA")) is ParseFailure)
        Assertions.assertTrue(automatonAB.consume(StringCodePointReader("AB")) is ParseSuccess)
        Assertions.assertTrue(automatonAB.consume(StringCodePointReader("BA")) is ParseFailure)
        Assertions.assertTrue(automatonAB.consume(StringCodePointReader("BB")) is ParseFailure)
    }

    @Test
    fun testRepeat() {
        // Arrange
        val automatonA = Automaton.ofString<Nothing>("A")
        val automatonAs = automatonA.repeat().then(eofAutomaton)

        // Act / Assert
        Assertions.assertTrue(automatonAs.consume(StringCodePointReader("")) is ParseFailure)
        Assertions.assertTrue(automatonAs.consume(StringCodePointReader("A")) is ParseSuccess)
        Assertions.assertTrue(automatonAs.consume(StringCodePointReader("AAAA")) is ParseSuccess)
        Assertions.assertTrue(automatonAs.consume(StringCodePointReader("B")) is ParseFailure)
    }

    @Test
    fun testOptional() {
        // Arrange
        val automatonA = Automaton.ofString<Nothing>("A")
        val automatonAopt = automatonA.optional().then(eofAutomaton)

        // Act / Assert
        Assertions.assertTrue(automatonAopt.consume(StringCodePointReader("")) is ParseSuccess)
        Assertions.assertTrue(automatonAopt.consume(StringCodePointReader("A")) is ParseSuccess)
        Assertions.assertTrue(automatonAopt.consume(StringCodePointReader("B")) is ParseFailure)
    }
}