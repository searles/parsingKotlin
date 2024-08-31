package at.searles.parsing.lexer.regexp

import at.searles.parsing.lexer.Automaton

class Concat(val first: Regexp, val second: Regexp): Regexp {
    override fun <A> toAutomaton(): Automaton<A> {
        return first.toAutomaton<A>().then(second.toAutomaton())
    }

    override fun toString(): String {
        return "$first$second"
    }
}