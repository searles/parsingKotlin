package at.searles.parsing.parser

import at.searles.parsing.ParseResult
import at.searles.parsing.parser.combinators.ParserThenParser
import at.searles.parsing.parser.combinators.PassThroughWrapper
import at.searles.parsing.parser.combinators.RecognizerThenRecognizer
import at.searles.parsing.reader.PositionReader

interface Recognizer {
    fun parse(reader: PositionReader): ParseResult<Unit>
    fun print(): PrintResult<Unit>

    operator fun <A, B> plus(parser: Parser<A, B>): Parser<A, B> {
        return ParserThenParser(this.passThough(), parser)
    }

    operator fun plus(recognizer: Recognizer): Recognizer {
        return RecognizerThenRecognizer(this, recognizer)
    }

    fun <A> passThough(): Parser<A, A> {
        return PassThroughWrapper(this)
    }
}