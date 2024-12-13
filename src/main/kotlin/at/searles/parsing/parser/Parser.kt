package at.searles.parsing.parser

import at.searles.parsing.ParseResult
import at.searles.parsing.parser.combinators.*
import at.searles.parsing.reader.PositionReader

interface Parser<A, B> {
    fun parse(input: A, reader: PositionReader): ParseResult<B>
    fun print(value: B): PrintResult<A>

    operator fun <C> plus(parser: Parser<B, C>): Parser<A, C> {
        return ParserThenParser(this, parser)
    }

    operator fun plus(recognizer: Recognizer): Parser<A, B> {
        return ParserThenParser(this, recognizer.passThough())
    }

    infix fun or(other: Parser<A, B>): Parser<A, B> {
        return ParserOrParser(this, other)
    }

    companion object {
        inline fun <reified A> Parser<A, A>.rep(minCount: Int = 0, maxCount: Int? = null): Parser<A, A> {
            return RepeatParser(this, minCount, maxCount)
        }

        inline fun <reified A> Parser<A, A>.opt(): Parser<A, A> {
            return RepeatParser(this, 0, 1)
        }

        inline fun <reified A, reified B, reified C> Parser<Unit, B>.fold(fold: FoldAction<A, B, C>): Parser<A, C> {
            return FoldAppliedToParser(fold, this)
        }
    }
}
