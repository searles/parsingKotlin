package at.searles.parsing.grammars

import at.searles.parsing.lexer.Lexer
import at.searles.parsing.lexer.WithLexer
import at.searles.parsing.lexer.regexp.*
import at.searles.parsing.parser.*
import at.searles.parsing.parser.Parser.Companion.fold
import at.searles.parsing.parser.Parser.Companion.rep
import at.searles.parsing.parser.utils.ListUtils
import at.searles.parsing.reader.CodePointSequence

object RegexpGrammar: WithLexer {
    override val lexer = Lexer()

    val regexp by ref { expression }

    val expression: Parser<Unit, Regexp> by ref { term + ("|".recognizer.passThough<Regexp>() + term.fold(FoldAction { r0, r1 -> r0 or r1})).rep() }

    val term by ref { factor + (factor.fold(FoldAction<Regexp, Regexp, Regexp> { r0, r1 -> r0 + r1})).rep() }

    val factor by ref { base + quantifier.rep() }
    
    val base by ref {
        ".".recognizer + MapAction.init { Regexp.ranges(0 .. Int.MAX_VALUE) } or
                "[".recognizer + characterClass + "]".recognizer + MapAction { Regexp.ranges(it) } or
                singleChar + MapAction { Regexp.chars(it) } or
                "(".recognizer + expression + ")".recognizer.passThough()
    }

    val quantifier by ref {
        "*".recognizer.passThough<Regexp>() + MapAction { it.rep() } or
                "+".recognizer.passThough<Regexp>() + MapAction { it.plus() } or
                "?".recognizer.passThough<Regexp>() + MapAction { it.opt() }
    }

    val characterClass by ref {
        "^".recognizer + characterRanges + InvertRanges or
                characterRanges
    }

    val characterRanges by ref {
        ListUtils.empty<IntRange>() + (characterRange.fold(ListUtils.append())).rep(1)
    }

    val characterRange by ref {
        singleChar + (
                ("-".recognizer.passThough<Int>() + singleChar.fold { first, last -> first .. last } ) or
                        MapAction { it .. it }
        )
    }

    val singleChar by ref {
        specialChar.parser + SpecialChar or
        character.parser + MapAction { it[0] }
    }

    /*
    <regex>         ::= <expression>

<expression>    ::= <term> '|' <expression>       (* alternation *)
                  | <term>

<term>          ::= <factor> <term>               (* concatenation *)
                  | <factor>

<factor>        ::= <base> <quantifier>           (* quantification *)
                  | <base>

<base>          ::= <character>                   (* a single character *)
                  | '.'                           (* any character except newline *)
                  | '\' <special_char>            (* escaped special character *)
                  | '[' <character_class> ']'     (* character class *)
                  | '(' <expression> ')'          (* grouped expression *)

<quantifier>    ::= '*'                           (* zero or more occurrences *)
                  | '+'                           (* one or more occurrences *)
                  | '?'                           (* zero or one occurrence *)

<character_class> ::= '^'? <character_range>+     (* character class with optional negation *)
<character_range>  ::= <character> '-' <character> (* character range in a class, e.g., a-z *)
                     | <character>                (* individual character *)

<special_char>  ::= '.' | '*' | '+' | '?' | '|' | '(' | ')' | '[' | ']' | '{' | '}' | '^' | '$' | '\'  (* escaped special characters *)

<character>     ::= any non-special character     (* any character except special ones *)
<number>        ::= digit+                        (* one or more digits *)
     */

    val number = Regexp.ranges('0'.code .. '9'.code).plus()
    val hexDigit = Regexp.ranges('0'.code .. '9'.code, 'A'.code .. 'F'.code, 'a'.code .. 'f'.code)
    val character = Regexp.ranges(0 .. Integer.MAX_VALUE)
    val specialChar = Text("\\u") + hexDigit.count(4) or
            Text("\\U") + hexDigit.count(8) or
            Text("\\") + Regexp.chars('n'.code, 'r'.code, '.'.code, '\\'.code, '0'.code) // TODO more

    object InvertRanges: MapAction<List<IntRange>, List<IntRange>> {
        override fun convert(value: List<IntRange>): List<IntRange> {
            var firstExclusive = -1
            val ranges = mutableListOf<IntRange>()

            for (range in value.sortedBy { it.first }) {
                if (firstExclusive < range.first - 1) {
                    ranges.add(firstExclusive + 1 until range.first)
                }

                firstExclusive = range.last
            }

            if (firstExclusive < Int.MAX_VALUE) {
                ranges.add(firstExclusive + 1 .. Int.MAX_VALUE)
            }

            return ranges
        }
    }

    object SpecialChar: MapAction<CodePointSequence, Int> {
        override fun convert(value: CodePointSequence): Int {
            return when (value[1]) {
                'u'.code -> value.toString().substring(2).toInt(16)
                'U'.code -> value.toString().substring(2).toInt(16)
                'n'.code -> '\n'.code
                'r'.code -> '\r'.code
                '0'.code -> 0
                // TODO more
                else -> value[1]
            }
        }
    }
}