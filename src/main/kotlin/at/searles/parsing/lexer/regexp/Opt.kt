package at.searles.parsing.lexer.regexp

import at.searles.parsing.lexer.Automaton

class Opt(private val regexp: Regexp): Regexp {
    override fun toAutomaton(): Automaton {
        return regexp.toAutomaton().opt()
    }
}