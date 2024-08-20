package at.searles.parsing.parser

import at.searles.parsing.Result
import at.searles.parsing.parser.combinators.*
import at.searles.parsing.reader.PositionReader

interface Parser<A> {
    fun parse(reader: PositionReader): Result<A>

    operator fun <B> plus(reducer: Reducer<A, B>): Parser<B> {
        return ParserThenReducer(this, reducer)
    }

    operator fun <A0, C> plus(fold: Fold<A0, A, C>): Reducer<A0, C> {
        return ParserThenFold(this, fold)
    }

    operator fun <B> plus(converter: Converter<A, B>): Parser<B> {
        return ParserThenConverter(this, converter)
    }

    operator fun plus(recognizer: Recognizer): Parser<A> {
        return ParserThenRecognizer(this, recognizer)
    }

    infix fun or(other: Parser<A>): Parser<A> {
        return ParserOrParser(this, other)
    }
}
