package at.searles.parsing.parser

import at.searles.parsing.PrintResult
import at.searles.parsing.ParseResult
import at.searles.parsing.parser.combinators.*
import at.searles.parsing.reader.PositionReader

interface Parser<A> {
    fun parse(reader: PositionReader): ParseResult<A>
    fun print(value: A): PrintResult

    operator fun <B> plus(reducer: Reducer<A, B>): Parser<B> {
        return ParserThenReducer(this, reducer)
    }

    operator fun <A0, C> plus(foldAction: FoldAction<A0, A, C>): Reducer<A0, C> {
        return ParserThenFold(this, foldAction)
    }

    operator fun plus(recognizer: Recognizer): Parser<A> {
        return ParserThenRecognizer(this, recognizer)
    }

    operator fun plus(consumeAction: ConsumeAction<A>): Recognizer {
        return ParserThenConsume(this, consumeAction)
    }

    infix fun or(other: Parser<A>): Parser<A> {
        return ParserOrParser(this, other)
    }
}
