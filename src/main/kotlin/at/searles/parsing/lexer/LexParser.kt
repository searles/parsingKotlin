package at.searles.parsing.lexer

import at.searles.parsing.*
import at.searles.parsing.parser.Parser
import at.searles.parsing.parser.PrintResult
import at.searles.parsing.parser.PrintSuccess
import at.searles.parsing.reader.CodePointSequence
import at.searles.parsing.reader.PositionReader

class LexParser(private val label: Label, val lexer: Lexer): Parser<Unit, CodePointSequence> {
    override fun parse(input: Unit, reader: PositionReader): ParseResult<CodePointSequence> {
        return when (val result = reader.accept(lexer)) {
            is ParseSuccess -> if (label in result.value) {
                return ParseSuccess(reader.getSequence(result.start, result.end), result.start, result.end)
            } else {
                reader.position = result.start
                return ParseFailure(result.start)
            }
            is ParseFailure -> result
        }
    }

    override fun print(value: CodePointSequence): PrintResult<Unit> {
        return PrintSuccess(Unit, TextOutputTree(value))
    }
}
