package at.searles.parsing

sealed class InvertResult<out A>
data class InvertSuccess<out A>(val value: A): InvertResult<A>()
data object InvertFailure: InvertResult<Nothing>()
