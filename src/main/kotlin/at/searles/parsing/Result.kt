package at.searles.parsing

sealed class Result<out A>
data class Success<out A>(val value: A, val start: Long, val end: Long): Result<A>()
data object Failure: Result<Nothing>()
