package at.searles.parsing.lexer.regexp

import at.searles.parsing.lexer.Automaton

class Choice(private val regexp0: Regexp, private val regexp1: Regexp): Regexp {
    override fun <A> toAutomaton(): Automaton<A> {
        return regexp0.toAutomaton<A>().or(regexp1.toAutomaton())
    }

    override fun toString(): String {
        return "$regexp0 | $regexp1"
    }
}