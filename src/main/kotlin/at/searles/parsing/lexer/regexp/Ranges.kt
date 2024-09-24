package at.searles.parsing.lexer.regexp

import at.searles.parsing.lexer.Automaton

class Ranges(val ranges: List<IntRange>) : Regexp {
    override fun toAutomaton(): Automaton {
        return Automaton.ofRange(ranges)
    }

    override fun toString(): String {
        return "[${ranges.joinToString(", ")}}]"
    }
}