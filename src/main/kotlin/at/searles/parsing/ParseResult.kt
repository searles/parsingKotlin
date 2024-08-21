package at.searles.parsing

sealed interface ParseResult<out A>
data class ParseSuccess<out A>(val value: A, val start: Long, val end: Long): ParseResult<A>
data class ParseFailure(val position: Long, val reason: String? = null): ParseResult<Nothing>
