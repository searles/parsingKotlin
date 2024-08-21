package at.searles.parsing.parser

import at.searles.parsing.InvertResult

fun interface Converter<A, B> {
    fun convert(value: A): B
    fun invert(result: B): InvertResult<A> {
        error("No inverse defined")
    }
}