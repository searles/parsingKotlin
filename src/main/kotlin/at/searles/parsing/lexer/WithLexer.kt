package at.searles.parsing.lexer

import at.searles.parsing.lexer.regexp.Regexp
import at.searles.parsing.lexer.regexp.Text
import at.searles.parsing.parser.ConsumeAction
import at.searles.parsing.reader.CodePointSequence.Companion.asCodePointSequence

interface WithLexer {
    val lexer: Lexer
    val Regexp.parser get() = LexParser(lexer.add(this), lexer)
    val String.recognizer get() = Text(this).parser + ConsumeAction { this.asCodePointSequence() }
}