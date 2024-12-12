package at.searles.parsing.grammar

import at.searles.parsing.ParseSuccess
import at.searles.parsing.grammars.RegexpGrammar
import at.searles.parsing.reader.CodePointSequence.Companion.asCodePointSequence
import org.junit.jupiter.api.Assertions
import kotlin.test.Test

class RegexpGrammarTest {
    @Test
    fun `test simple regexp can be parsed`() {
        val regexpString = "[^0-9]+"

        // Act
        val regexp = RegexpGrammar.regexp.parse(Unit, regexpString.asCodePointSequence().toReader())

        // Assert
        Assertions.assertTrue(regexp is ParseSuccess)
    }
}