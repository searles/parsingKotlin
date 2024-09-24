package at.searles.parsing.grammars

import at.searles.parsing.InvertFailure
import at.searles.parsing.InvertResult
import at.searles.parsing.InvertSuccess
import at.searles.parsing.lexer.Lexer
import at.searles.parsing.lexer.WithLexer
import at.searles.parsing.lexer.regexp.*
import at.searles.parsing.parser.*
import at.searles.parsing.parser.Reducer.Companion.rep
import at.searles.parsing.reader.CodePointSequence

object RegexpGrammar: WithLexer {
    override val lexer = Lexer()
    
    val regexp = self { expression }

    val expression: Parser<Regexp> by lazy { self { term + ("|".recognizer + term + FoldAction<Regexp, Regexp, Regexp> { r0, r1 -> r0 or r1}).rep() } }

    val term by lazy { factor + (factor + FoldAction<Regexp, Regexp, Regexp> { r0, r1 -> r0 + r1}).rep() }

    val factor by lazy { base + quantifier.rep() }

    val base by lazy {
        ".".recognizer + InitAction { Regexp.ranges(0 .. Int.MAX_VALUE) } or
                "[".recognizer + characterClass + "]".recognizer + MapAction { Regexp.ranges(it) } or
                singleChar + MapAction { Regexp.chars(it) } or
                "(".recognizer + expression + ")".recognizer
    }

    val quantifier: Reducer<Regexp, Regexp> by lazy {
        "*".recognizer + MapAction<Regexp, Regexp> { it.rep() } or
                "+".recognizer + MapAction { it.plus() } or
                "?".recognizer + MapAction { it.opt() }
    }

    val characterClass by lazy {
        "^".recognizer + characterRanges + InvertRanges or
                characterRanges
    }

    val characterRanges by lazy {
        emptyList<IntRange>() + (characterRange + append()).rep(1)
    }

    val characterRange by lazy {
        singleChar + (
                ("-".recognizer + singleChar + FoldAction<Int, Int, IntRange> { first, last -> first .. last } ) or
                        MapAction { it .. it }
        )
    }

    val singleChar by lazy {
        specialChar.parser + SpecialChar or
        character.parser + MapAction { it[0] }
    }

    fun <A> emptyList(): InitAction<List<A>> {
        return object : InitAction<List<A>> {
            override fun init(): List<A> {
                return kotlin.collections.emptyList()
            }

            override fun invert(result: List<A>): InvertResult<Unit> {
                return when {
                    result.isEmpty() -> InvertSuccess(Unit)
                    else -> InvertFailure
                }
            }
        }
    }

    fun <A> append(): FoldAction<List<A>, A, List<A>> {
        return object : FoldAction<List<A>, A, List<A>> {
            override fun fold(left: List<A>, right: A): List<A> {
                return left + right
            }

            override fun leftInverse(result: List<A>): InvertResult<List<A>> {
                return when {
                    result.isEmpty() -> InvertFailure
                    else -> InvertSuccess(result.dropLast(1))
                }
            }

            override fun rightInverse(result: List<A>): InvertResult<A> {
                return when {
                    result.isEmpty() -> InvertFailure
                    else -> InvertSuccess(result.last())
                }
            }
        }
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