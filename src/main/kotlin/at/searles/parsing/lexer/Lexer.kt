package at.searles.parsing.lexer

import at.searles.parsing.ParseResult
import at.searles.parsing.lexer.regexp.Regexp
import at.searles.parsing.reader.Consumer
import at.searles.parsing.reader.PositionReader

class Lexer(private val labelStream: LabelStream = LabelStream()): Consumer<Set<Label>> {
    private var automaton = Automaton.nothing()

    fun add(regexp: Regexp): Label {
        val newLabel = labelStream.next()
        val regexpAutomaton = regexp.toAutomaton().applyLabel(newLabel)
        automaton = automaton.or(regexpAutomaton)

        return when (val oldLabel = automaton.findEquivalentLabel(newLabel)) {
            null -> newLabel
            else -> {
                automaton.removeLabel(newLabel)
                oldLabel
            }
        }
    }

    override fun consume(reader: PositionReader): ParseResult<Set<Label>> {
        return reader.accept(automaton)
    }
}