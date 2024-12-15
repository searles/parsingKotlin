package at.searles.parsing.grammars

import at.searles.parsing.ParseResult
import at.searles.parsing.TextOutputTree
import at.searles.parsing.lexer.Lexer
import at.searles.parsing.lexer.WithLexer
import at.searles.parsing.lexer.regexp.Regexp
import at.searles.parsing.parser.*
import at.searles.parsing.parser.Parser.Companion.fold
import at.searles.parsing.parser.Parser.Companion.rep
import at.searles.parsing.parser.utils.StringUtils
import at.searles.parsing.reader.CodePointSequence.Companion.asCodePointSequence
import at.searles.parsing.reader.PositionReader
import java.math.BigInteger

object Common {
    const val MAX_CODEPOINT = 0x10FFF

    object DoubleQuotedString: WithLexer, Parser<Unit, String> {
        override val lexer: Lexer = Lexer()

        val hexDigit =
            Regexp.ranges('0'.code .. '9'.code, 'A'.code .. 'F'.code, 'a'.code .. 'f'.code)

        val hexDigit4 = hexDigit.count(4).parser + MapAction { it.toString().toInt(16) }
        val hexDigit8 = hexDigit.count(8).parser + MapAction { it.toString().toInt(16) }
        val hexDigits = hexDigit.plus().parser + MapAction { it.toString().toInt(16) }

        val specialChar =
            "\\n".recognizer + MapAction.init { '\n'.code } or
            "\\r".recognizer + MapAction.init { '\r'.code } or
            "\\t".recognizer + MapAction.init { '\t'.code } or
            "\\u".recognizer + hexDigit4 or
            "\\u".recognizer + "{".recognizer + hexDigits + "}".recognizer.passThough() or
            "\\U".recognizer + hexDigit8 or
            "\\".recognizer + Regexp.ranges(0 .. MAX_CODEPOINT).parser + MapAction { it[0] }

        // everything except '\\'=92 and '"'=34
        val singleChar = Regexp.ranges(0 ..< '"'.code, ('"'.code + 1) ..< '\\'.code, ('\\'.code + 1) .. MAX_CODEPOINT).parser + MapAction { it[0] }

        val codePoint = specialChar or singleChar

        val chars = StringUtils.Empty +
                ( codePoint.fold(StringUtils.Append)).rep()

        val string = "\"".recognizer + chars + "\"".recognizer.passThough()

        override fun parse(input: Unit, reader: PositionReader): ParseResult<String> {
            return string.parse(Unit, reader)
        }

        override fun print(value: String): PrintResult<Unit> {
            return string.print(value)
        }
    }

    object SingleQuotedString: WithLexer, Parser<Unit, String> {
        val specialChar = "\\'".recognizer + MapAction.init { '\''.code } or
                "\\\\".recognizer + MapAction.init { '\\'.code }

        // everything except '\\'=92 and '\''=39
        val singleChar = Regexp.ranges(0 ..< '\''.code, ('\''.code + 1) ..< '\\'.code, ('\\'.code + 1) .. MAX_CODEPOINT).parser + MapAction { it[0] }

        val codePoint = specialChar or singleChar

        val chars = MapAction.init { StringBuilder() } +
                ( codePoint.fold(FoldAction<StringBuilder, Int, StringBuilder> { sb, cp -> sb.apply { appendCodePoint(cp) } } )).rep() +
                MapAction { it.toString() }

        val string = "\'".recognizer + chars + "\'".recognizer.passThough()

        override val lexer: Lexer = Lexer()

        override fun parse(input: Unit, reader: PositionReader): ParseResult<String> {
            return string.parse(Unit, reader)
        }

        override fun print(value: String): PrintResult<Unit>{
            return string.print(value)
        }
    }

    object IntNumber: WithLexer, Parser<Unit, BigInteger> {
        override val lexer: Lexer = Lexer()

        val ten = BigInteger.valueOf(10)
        val digit = Regexp.ranges('0'.code .. '9'.code).parser + MapAction { BigInteger.valueOf((it[0] - '0'.code).toLong()) }
        val number = digit + MapAction { BigInteger.valueOf(it.toLong()) } + (digit.fold { n, i -> n * ten + i } )

        override fun parse(input: Unit, reader: PositionReader): ParseResult<BigInteger> {
            return number.parse(Unit, reader)
        }

        override fun print(value: BigInteger): PrintResult<Unit>{
            TODO("Not yet implemented")
        }

    }

    object BooleanParser: WithLexer, Parser<Unit, Boolean> {
        override val lexer: Lexer = Lexer()

        val parser = "true".recognizer + MapAction.init { true } or
                "false".recognizer + MapAction.init { false }

        override fun parse(input: Unit, reader: PositionReader): ParseResult<Boolean> {
            return parser.parse(Unit, reader)
        }

        override fun print(value: Boolean): PrintResult<Unit>{
            return PrintSuccess(Unit, TextOutputTree(value.toString().asCodePointSequence()))
        }

    }
}