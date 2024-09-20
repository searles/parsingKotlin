package at.searles.parsing.parser

fun interface ConsumeAction<A> {
    fun invert(): A
}