package at.searles.parsing.lexer

import at.searles.parsing.ParseResult
import at.searles.parsing.lexer.regexp.Regexp
import at.searles.parsing.reader.Consumer
import at.searles.parsing.reader.PositionReader

class Lexer<A>: Consumer<Set<A>> {
    private var automaton = Automaton.nothing<A>()
    private var regexps = mutableMapOf<A, Regexp>()

    fun add(regexp: Regexp, label: A) {
        automaton = automaton.or(regexp.toAutomaton<A>().applyLabel(label))
        regexps[label] = regexp
    }

    override fun consume(reader: PositionReader): ParseResult<Set<A>> {
        return reader.accept(automaton)
    }
}