package at.searles.parsing.lexer

import at.searles.parsing.lexer.regexp.Regexp
import at.searles.parsing.lexer.regexp.Text
import at.searles.parsing.parser.MapAction
import at.searles.parsing.parser.Recognizer
import at.searles.parsing.reader.CodePointSequence.Companion.asCodePointSequence

interface WithLexer {
    val lexer: Lexer
    val Regexp.parser: LexParser get() = LexParser(lexer.add(this), lexer)
    val String.recognizer: Recognizer get() = Text(this).parser.asRecognizer(this.asCodePointSequence())
    val eof get() = LexParser(lexer.add(Regexp.chars(-1)), lexer) + MapAction.elim { "".asCodePointSequence() }
}