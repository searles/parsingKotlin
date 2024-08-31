package at.searles.parsing.lexer.regexp

import at.searles.parsing.lexer.Automaton

class Ranges(private vararg val ranges: IntRange) : Regexp {
    override fun <A> toAutomaton(): Automaton<A> {
        return Automaton.ofRange(*ranges)
    }

    override fun toString(): String {
        return "[${ranges.joinToString(", ")}}]"
    }
}