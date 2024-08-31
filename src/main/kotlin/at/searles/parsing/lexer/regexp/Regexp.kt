package at.searles.parsing.lexer.regexp

import at.searles.parsing.lexer.Automaton

interface Regexp {
    fun <A> toAutomaton(): Automaton<A>
}