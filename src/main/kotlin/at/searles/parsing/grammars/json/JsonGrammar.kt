package at.searles.parsing.grammars.json

import at.searles.parsing.grammars.Common
import at.searles.parsing.lexer.Lexer
import at.searles.parsing.lexer.WithLexer
import at.searles.parsing.parser.MapAction
import at.searles.parsing.parser.Parser
import at.searles.parsing.parser.Parser.Companion.fold
import at.searles.parsing.parser.Parser.Companion.rep
import at.searles.parsing.parser.ref
import at.searles.parsing.parser.utils.ListUtils
import at.searles.parsing.parser.utils.TypeUtils.cast
import java.math.BigInteger

object JsonGrammar: WithLexer {
    override val lexer: Lexer = Lexer()

    // <json> ::= <object> | <array>
    val json by ref {
        jsonObject + cast<Map<String, Any>, Any>() or
        jsonArray + cast<List<Any>, Any>()
    }

    // <object> ::= '{' [ <members> ] '}'
    val jsonObject by ref { "{".recognizer + members + "}".recognizer.passThough() + MapAction { it.toMap() } }

    // <members> ::= <pair> | <pair> ',' <members>
    val members by ref { ListUtils.empty<Pair<String, Any>>() + (pair.fold(ListUtils.append())).rep() }

    // <pair> ::= <string> ':' <value>
    val pair by ref { Common.DoubleQuotedString + ":".recognizer.passThough() + (value.fold { key, value -> key to value }) }

    // <array> ::= '[' [ <elements> ] ']'
    val jsonArray by ref { "[".recognizer + (elements or ListUtils.empty()) + "]".recognizer.passThough() }

    // <elements> ::= <value> | <value> ',' <elements>
    val elements by ref { value + ListUtils.wrap() + ( ",".recognizer.passThough<List<Any>>() + value.fold(ListUtils.append(1))).rep() }

    // <value> ::= <string>
    //          | <number>
    //          | <object>
    //          | <array>
    //          | 'true'
    //          | 'false'
    //          | 'null'
    val value: Parser<Unit, Any> by ref {
        Common.DoubleQuotedString + cast<String, Any>() or
                Common.IntNumber + cast<BigInteger, Any>() or
                jsonObject + cast<Map<String, Any>, Any>() or
                jsonArray + cast<List<Any>, Any>() or
                Common.BooleanParser + cast<Boolean, Any>()
    }

    // -- Lexer --

//    <string> ::= '"' <char> '"'
//    <char> ::= <any character except " or \> | '\\' <escaped_char>
//
//    <escaped_char> ::= '"' | '\' | '/' | 'b' | 'f' | 'n' | 'r' | 't' | 'u' <hex>
//
//    <hex> ::= <hex_digit> <hex_digit> <hex_digit> <hex_digit>
//
//    <hex_digit> ::= '0' | '1' | '2' | '3' | '4' | '5' | '6' | '7' | '8' | '9'
//                  | 'a' | 'b' | 'c' | 'd' | 'e' | 'f'
//                  | 'A' | 'B' | 'C' | 'D' | 'E' | 'F'
//
//    <number> ::= [ '-' ] <digit> <digits>
//
//    <digit> ::= '0' | '1' | '2' | '3' | '4' | '5' | '6' | '7' | '8' | '9'
//    <digits> ::= <digit> | <digit> <digits>
}