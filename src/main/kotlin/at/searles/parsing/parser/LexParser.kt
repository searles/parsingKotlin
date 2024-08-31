package at.searles.parsing.parser

import at.searles.parsing.*
import at.searles.parsing.lexer.Lexer
import at.searles.parsing.reader.CodePointSequence
import at.searles.parsing.reader.PositionReader

class LexParser<A>(val lexem: A, val lexer: Lexer<A>): Parser<CodePointSequence> {
    override fun parse(reader: PositionReader): ParseResult<CodePointSequence> {
        return when (val result = reader.accept(lexer)) {
            is ParseSuccess -> if (lexem in result.value) {
                return ParseSuccess(reader.getSequence(result.start, result.end), result.start, result.end)
            } else {
                reader.position = result.start
                return ParseFailure(result.start)
            }
            is ParseFailure -> result
        }
    }

    override fun print(value: CodePointSequence): PrintResult {
        return TextPrintSuccess(value)
    }
}