package at.searles.parsing.lexer

import at.searles.parsing.lexer.regexp.Regexp
import at.searles.parsing.lexer.regexp.Text
import at.searles.parsing.parser.MapAction
import at.searles.parsing.parser.Parser
import at.searles.parsing.reader.CodePointSequence
import at.searles.parsing.reader.CodePointSequence.Companion.asCodePointSequence

interface WithLexer {
    val lexer: Lexer
    val Regexp.parser: Parser<Unit, CodePointSequence> get() = LexParser(lexer.add(this), lexer)
    val String.recognizer: Parser<Unit, Unit> get() = Text(this).parser + MapAction.elim { this.asCodePointSequence() }
    val eof get() = LexParser(lexer.add(Regexp.chars(-1)), lexer) + MapAction.elim { "".asCodePointSequence() }
}