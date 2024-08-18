package at.searles.parsing.parser

fun interface Converter<A, B> {
    fun convert(value: A): B
}