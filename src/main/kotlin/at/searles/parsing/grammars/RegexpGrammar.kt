package at.searles.parsing.grammars

import at.searles.parsing.InvertFailure
import at.searles.parsing.InvertResult
import at.searles.parsing.InvertSuccess
import at.searles.parsing.lexer.Lexer
import at.searles.parsing.lexer.WithLexer
import at.searles.parsing.lexer.regexp.Concat
import at.searles.parsing.lexer.regexp.Ranges
import at.searles.parsing.lexer.regexp.Regexp
import at.searles.parsing.lexer.regexp.Text
import at.searles.parsing.parser.*
import at.searles.parsing.parser.Reducer.Companion.rep
import at.searles.parsing.reader.CodePointSequence
import at.searles.parsing.reader.CodePointSequence.Companion.asCodePointSequence

object RegexpGrammar: WithLexer {
    override val lexer = Lexer()
    
    val regexp = self { expression }

    val expression: Parser<Regexp> by lazy { self { term + ("|".recognizer + term + Alternation).rep() } }

    val term by lazy { factor + (factor + Concatenation).rep() }

    val factor by lazy { base + quantifier }

    val base by lazy {
        ".".recognizer + AllChars or
                "[".recognizer + characterClass + "]".recognizer + CharClassToCodePoint or
                "(".recognizer + expression + ")".recognizer or
                singleChar + CodePoint
    }

    val quantifier: Reducer<Regexp, Regexp> by lazy {
        "*".recognizer + MapAction<Regexp, Regexp> { it.rep() } or
                "+".recognizer + MapAction { it.plus() } or
                "?".recognizer + MapAction { it.opt() } /*or
                "{".recognizer + number + "}".recognizer or
                "{".recognizer + number + ",".recognizer + "}".recognizer or
                "{".recognizer + number + ",".recognizer + number + "}".recognizer*/
    }

    val characterClass by lazy {
        "^".recognizer + characterRanges + InvertRanges or
                characterRanges
    }

    val characterRanges by lazy {
        emptyList<IntRange>() + (characterRange + append()).rep() // TODO Allow count (min = 1)
    }

    val characterRange by lazy {
        singleChar + (
                ("-".recognizer + singleChar + CreateRange) or
                        CharAsRange
        )
    }

    val singleChar by lazy {
        specialChar.parser + SpecialChar or
        character.parser + Char
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


//    abstract inner class Lex(regexp: Regexp? = null, txt: String? = null) {
//        fun init() {
//            if (regexp != null) {
//                lexer.add(regexp, this)
//            } else if (txt != null) {
//                lexer.add(Text(txt), this)
//            }
//        }
//    }



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
                  | '{' <number> '}'              (* exact number of occurrences *)
                  | '{' <number> ',' '}'          (* at least number of occurrences *)
                  | '{' <number> ',' <number> '}' (* range of occurrences *)

<character_class> ::= '^'? <character_range>+     (* character class with optional negation *)
<character_range>  ::= <character> '-' <character> (* character range in a class, e.g., a-z *)
                     | <character>                (* individual character *)

<special_char>  ::= '.' | '*' | '+' | '?' | '|' | '(' | ')' | '[' | ']' | '{' | '}' | '^' | '$' | '\'  (* escaped special characters *)

<character>     ::= any non-special character     (* any character except special ones *)
<number>        ::= digit+                        (* one or more digits *)
     */

    val number = Ranges('0'.code .. '9'.code).plus()
    val character = Ranges(0 .. Integer.MAX_VALUE)
    val specialChar = Text("\\") + character // TODO uXXXX, UAAAAAAAA

    object Alternation: FoldAction<Regexp, Regexp, Regexp> {
        override fun fold(left: Regexp, right: Regexp): Regexp {
            TODO("Not yet implemented")
        }

        override fun leftInverse(result: Regexp): InvertResult<Regexp> {
            return super.leftInverse(result)
        }

        override fun rightInverse(result: Regexp): InvertResult<Regexp> {
            return super.rightInverse(result)
        }
    }

    object Concatenation: FoldAction<Regexp, Regexp, Regexp> {
        override fun fold(left: Regexp, right: Regexp): Regexp {
            return Concat(left, right)
        }

        override fun leftInverse(result: Regexp): InvertResult<Regexp> {
            return super.leftInverse(result)
        }

        override fun rightInverse(result: Regexp): InvertResult<Regexp> {
            return super.rightInverse(result)
        }
    }

    object NumberToInt: MapAction<CodePointSequence, Int> {
        override fun convert(value: CodePointSequence): Int {
            return value.toReader().fold(0) { n, cp -> n * 10 + cp }
        }

        override fun invert(result: Int): InvertResult<CodePointSequence> {
            return InvertSuccess(result.toString().asCodePointSequence())
        }
    }

    object CharAsRange: MapAction<Int, IntRange> {
        override fun convert(value: Int): IntRange {
            return value .. value
        }

        override fun invert(result: IntRange): InvertResult<Int> {
            return when {
                result.first == result.last -> InvertSuccess(result.first)
                else -> InvertFailure
            }
        }
    }

    object CharToCodePoint: MapAction<CodePointSequence, Int> {
        override fun convert(value: CodePointSequence): Int {
            return value[0]
        }

        override fun invert(result: Int): InvertResult<CodePointSequence> {
            return InvertSuccess(CodePointSequence.fromCodePoint(result))
        }
    }

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

        override fun invert(result: List<IntRange>): InvertResult<List<IntRange>> {
            return when (result.first().first) {
                0 -> InvertSuccess(convert(result))
                else -> InvertFailure
            }
        }
    }

    object CreateRange: FoldAction<Int, Int, IntRange> {
        override fun fold(left: Int, right: Int): IntRange {
            return IntRange(left, right)
        }

        override fun leftInverse(result: IntRange): InvertResult<Int> {
            return InvertSuccess(result.first)
        }

        override fun rightInverse(result: IntRange): InvertResult<Int> {
            return InvertSuccess(result.last)
        }
    }

    object AllChars: InitAction<Regexp> {
        override fun init(): Regexp {
            return Ranges(0 .. Int.MAX_VALUE)
        }
    }

    object CodePoint: MapAction<Int, Regexp> {
        override fun convert(value: Int): Regexp {
            return Ranges(value .. value)
        }
    }

    object SpecialChar: MapAction<CodePointSequence, Int> {
        override fun convert(value: CodePointSequence): Int {
            TODO("Not yet implemented")
        }
    }

    object Char: MapAction<CodePointSequence, Int> {
        override fun convert(value: CodePointSequence): Int {
            return value[0]
        }
    }

    object CharClassToCodePoint: MapAction<List<IntRange>, Regexp> {
        override fun convert(value: List<IntRange>): Regexp {
            return Ranges(*value.toTypedArray())
        }
    }
}