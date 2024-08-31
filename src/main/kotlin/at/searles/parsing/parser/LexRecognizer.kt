package at.searles.parsing.parser

import at.searles.parsing.*
import at.searles.parsing.lexer.Lexer
import at.searles.parsing.reader.CodePointSequence
import at.searles.parsing.reader.PositionReader

class LexRecognizer<A>(private val lex: A, private val lexer: Lexer<A>, private val textRepresentation: () -> CodePointSequence): Recognizer {
    override fun parse(reader: PositionReader): ParseResult<Unit> {
        return when (val result = reader.accept(lexer)) {
            is ParseSuccess -> if (lex in result.value) {
                return ParseSuccess(Unit, result.start, result.end)
            } else {
                reader.position = result.start
                return ParseFailure(result.start)
            }
            is ParseFailure -> result
        }
    }

    override fun print(): PrintResult {
        return TextPrintSuccess(textRepresentation())
    }
}