package at.searles.parsing.parser.utils

import at.searles.parsing.InvertResult
import at.searles.parsing.InvertSuccess
import at.searles.parsing.parser.FoldAction

object Utils {
    fun <A, B> toPair(): FoldAction<A, B, Pair<A, B>> {
        return object : FoldAction<A, B, Pair<A, B>> {
            override fun fold(left: A, right: B): Pair<A, B> {
                return left to right
            }

            override fun leftInverse(result: Pair<A, B>): InvertResult<A> {
                return InvertSuccess(result.first)
            }

            override fun rightInverse(result: Pair<A, B>): InvertResult<B> {
                return InvertSuccess(result.second)
            }
        }
    }
}