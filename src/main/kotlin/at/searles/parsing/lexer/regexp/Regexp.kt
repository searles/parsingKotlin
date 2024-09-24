package at.searles.parsing.lexer.regexp

import at.searles.parsing.lexer.Automaton

interface Regexp {
    fun toAutomaton(): Automaton

    fun rep(): Regexp {
        return Rep(this)
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

    fun count(count: Int): Regexp {
        if (count <= 0) return empty
        return (1 .. count).fold(this) { rex, _ -> rex + this }
    }

    companion object {
        val empty = object : Regexp {
            override fun toAutomaton(): Automaton {
                return Automaton.empty()
            }
        }

        fun chars(vararg chars: Int): Regexp {
            return Ranges(chars.map { it .. it })
        }

        fun ranges(vararg ranges: IntRange): Regexp {
            return ranges(ranges.toList())
        }

        fun ranges(ranges: List<IntRange>): Regexp {
            return Ranges(ranges)
        }
    }
}