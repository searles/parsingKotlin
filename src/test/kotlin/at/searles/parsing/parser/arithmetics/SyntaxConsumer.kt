package at.searles.parsing.parser.arithmetics

import at.searles.parsing.lexer.LexParser
import at.searles.parsing.lexer.Lexer
import at.searles.parsing.lexer.regexp.Ranges
import at.searles.parsing.lexer.regexp.Plus
import at.searles.parsing.lexer.regexp.Regexp
import at.searles.parsing.lexer.regexp.Text
import at.searles.parsing.parser.ConsumeAction
import at.searles.parsing.reader.CodePointSequence.Companion.asCodePointSequence

object Syntax {
    private val lexer = Lexer()
    val Regexp.parser get() = LexParser(lexer.add(this), lexer)
    val String.recognizer get() = Text(this).parser + ConsumeAction { this.asCodePointSequence() }
}
