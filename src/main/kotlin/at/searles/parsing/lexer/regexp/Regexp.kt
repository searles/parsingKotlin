package at.searles.parsing.lexer.regexp

import at.searles.parsing.lexer.Automaton

interface Regexp {
    fun toAutomaton(): Automaton

    fun rep(): Regexp {
        return this.opt().plus()
    }

    fun plus(): Regexp {
        return Plus(this)
    }

    fun opt(): Regexp {
        return Opt(this)
    }

    operator fun plus(next: Regexp): Regexp {
        return Concat(this, next)
    }

    infix fun or(other: Regexp): Regexp {
        return Choice(this, other)
    }
}