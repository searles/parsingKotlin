package at.searles.parsing.lexer.regexp

import at.searles.parsing.lexer.Automaton

class Concat(val left: Regexp, val right: Regexp): Regexp {
    override fun toAutomaton(): Automaton {
        return left.toAutomaton().then(right.toAutomaton())
    }

    override fun toString(): String {
        return "$left$right"
    }
}