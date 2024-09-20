package at.searles.parsing.lexer.regexp

import at.searles.parsing.lexer.Automaton

class Choice(private val regexp0: Regexp, private val regexp1: Regexp): Regexp {
    override fun toAutomaton(): Automaton {
        return regexp0.toAutomaton().or(regexp1.toAutomaton())
    }

    override fun toString(): String {
        return "$regexp0 | $regexp1"
    }
}