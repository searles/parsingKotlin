package at.searles.parsing.parser.utils

import at.searles.parsing.InvertFailure
import at.searles.parsing.InvertResult
import at.searles.parsing.InvertSuccess
import at.searles.parsing.parser.MapAction

object TypeUtils {
    inline fun <reified A: B, reified B> cast(): MapAction<A, B> {
        return object : MapAction<A, B> {
            override fun convert(value: A): B {
                return value
            }

            override fun invert(result: B): InvertResult<A> {
                return when (result) {
                    is A -> InvertSuccess(result)
                    else -> InvertFailure
                }
            }
        }
    }
}