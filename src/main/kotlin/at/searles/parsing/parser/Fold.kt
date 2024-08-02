package at.searles.parsing.parser

fun interface Fold<A, B, C> {
    fun fold(left: A, right: B): C
}