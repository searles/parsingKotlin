package at.searles.parsing.lexer.regexp

import at.searles.parsing.lexer.Automaton

class Text(private val text: String): Regexp {
    override fun toAutomaton(): Automaton {
        return Automaton.ofString(text)
    }

    override fun toString(): String {
        return text
    }
}