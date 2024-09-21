package at.searles.parsing.grammar

import at.searles.parsing.ParseSuccess
import at.searles.parsing.grammars.RegexpGrammar
import at.searles.parsing.lexer.LexParser
import at.searles.parsing.lexer.Lexer
import at.searles.parsing.lexer.regexp.Ranges
import at.searles.parsing.reader.CodePointSequence.Companion.asCodePointSequence
import org.junit.jupiter.api.Assertions
import kotlin.test.Test

class RegexpGrammarTest {
    @Test
    fun `test simple regexp can be parsed`() {
        val regexpString = "."

        // Act
        val lexer = Lexer()
        val label = lexer.add(Ranges(0 .. Int.MAX_VALUE))
        val regexp = LexParser(label, lexer).parse(regexpString.asCodePointSequence().toReader())

        // Assert
        Assertions.assertTrue(regexp is ParseSuccess)
    }
}