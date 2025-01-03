package at.searles.parsing.parser.utils

import at.searles.parsing.InvertFailure
import at.searles.parsing.InvertResult
import at.searles.parsing.InvertSuccess
import at.searles.parsing.parser.FoldAction
import at.searles.parsing.parser.MapAction

object ListUtils {
    fun <A> empty(): MapAction<Unit, List<A>> {
        return object : MapAction<Unit, List<A>> {
            override fun convert(value: Unit): List<A> {
                return emptyList()
            }

            override fun invert(result: List<A>): InvertResult<Unit> {
                return when {
                    result.isEmpty() -> InvertSuccess(Unit)
                    else -> InvertFailure
                }
            }

            override fun toString(): String {
                return "<emptyList>"
            }
        }
    }

    fun <A> wrap(): MapAction<A, List<A>> {
        return object : MapAction<A, List<A>> {
            override fun convert(value: A): List<A> {
                return listOf(value)
            }

            override fun invert(result: List<A>): InvertResult<A> {
                return when (result.size) {
                    1 -> InvertSuccess(result.first())
                    else -> InvertFailure
                }
            }

            override fun toString(): String {
                return "<wrapList>"
            }
        }
    }

    fun <A> append(minRemainingElements: Int = 0): FoldAction<List<A>, A, List<A>> {
        return object : FoldAction<List<A>, A, List<A>> {
            override fun fold(left: List<A>, right: A): List<A> {
                return left + right
            }

            override fun leftInverse(result: List<A>): InvertResult<List<A>> {
                return when {
                    result.size <= minRemainingElements -> InvertFailure
                    else -> InvertSuccess(result.dropLast(1))
                }
            }

            override fun rightInverse(result: List<A>): InvertResult<A> {
                return when {
                    result.size <= minRemainingElements -> InvertFailure
                    else -> InvertSuccess(result.last())
                }
            }

            override fun toString(): String {
                return "<appendList>"
            }
        }
    }

    fun <A, B> toMap(): MapAction<List<Pair<A, B>>, Map<A, B>> {
        return object : MapAction<List<Pair<A, B>>, Map<A, B>> {
            override fun convert(value: List<Pair<A, B>>): Map<A, B> {
                return value.toMap()
            }

            override fun invert(result: Map<A, B>): InvertResult<List<Pair<A, B>>> {
                return InvertSuccess(result.entries.map { Pair(it.key, it.value) })
            }

            override fun toString(): String {
                return "<toMap>"
            }
        }
    }
}