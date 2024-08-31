package at.searles.parsing.lexer.regexp

import at.searles.parsing.lexer.Automaton

class Plus(private val regexp: Regexp): Regexp {
    override fun <A> toAutomaton(): Automaton<A> {
        return regexp.toAutomaton<A>().plus()
    }
}