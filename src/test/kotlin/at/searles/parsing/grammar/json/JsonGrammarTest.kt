package at.searles.parsing.grammar.json

import at.searles.parsing.ParseSuccess
import at.searles.parsing.grammars.json.JsonGrammar
import at.searles.parsing.reader.CodePointSequence.Companion.asCodePointSequence
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class JsonGrammarTest {
    @Test
    fun `when parsing simple json then json is correctly parsed`() {
        // arrange
        val reader = "{\"a\":\"b\"}".asCodePointSequence().toReader()

        // act
        val result = JsonGrammar.jsonObject.parse(Unit, reader)

        // assert
        Assertions.assertTrue(result is ParseSuccess)
    }
}