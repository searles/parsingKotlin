package at.searles.parsing.parser.utils

import at.searles.parsing.InvertFailure
import at.searles.parsing.InvertResult
import at.searles.parsing.InvertSuccess
import at.searles.parsing.parser.FoldAction
import at.searles.parsing.parser.MapAction

object StringUtils {
    object Empty: MapAction<Unit, String> {
        override fun convert(value: Unit): String {
            return ""
        }

        override fun invert(result: String): InvertResult<Unit> {
            return if (result.isEmpty()) {
                InvertSuccess(Unit)
            } else {
                InvertFailure
            }
        }

        override fun toString(): String {
            return "<emptyString>"
        }
    }

    object Append : FoldAction<String, Int, String> {
        override fun fold(left: String, right: Int): String {
            return StringBuilder(left).appendCodePoint(right).toString()
        }

        override fun leftInverse(result: String): InvertResult<String> {
            if (result.isEmpty()) {
                return InvertFailure
            }

            return if (result.last().isLowSurrogate()) {
                InvertSuccess(result.dropLast(2))
            } else {
                InvertSuccess(result.dropLast(1))
            }
        }

        override fun rightInverse(result: String): InvertResult<Int> {
            if (result.isEmpty()) {
                return InvertFailure
            }

            return InvertSuccess(result.codePointAt(result.length - 1))
        }

        override fun toString(): String {
            return "<append>"
        }
    }
}