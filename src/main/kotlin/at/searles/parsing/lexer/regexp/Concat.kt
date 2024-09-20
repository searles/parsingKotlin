package at.searles.parsing.lexer.regexp

import at.searles.parsing.lexer.Automaton

class Concat(val first: Regexp, val second: Regexp): Regexp {
    override fun toAutomaton(): Automaton {
        return first.toAutomaton().then(second.toAutomaton())
    }

    override fun toString(): String {
        return "$first$second"
    }
}