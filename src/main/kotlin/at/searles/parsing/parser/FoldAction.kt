package at.searles.parsing.parser

import at.searles.parsing.InvertResult

fun interface FoldAction<A, B, C> {
    fun fold(left: A, right: B): C

    fun leftInverse(result: C): InvertResult<A> {
        error("No left inverse defined")
    }

    fun rightInverse(result: C): InvertResult<B> {
        error("No right inverse defined")
    }
}