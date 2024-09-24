package at.searles.parsing.lexer.regexp

import at.searles.parsing.lexer.Automaton

class Rep(val child: Regexp): Regexp {
    override fun toAutomaton(): Automaton {
        return child.toAutomaton().plus().opt()
    }
}