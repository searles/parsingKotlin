package at.searles.parsing

sealed interface ParseResult<out A> {
    val position: Long
}

data class ParseSuccess<out A>(val value: A, val start: Long, val end: Long): ParseResult<A> {
    override val position: Long get() = start
}

data class ParseFailure(override val position: Long, val reason: String? = null): ParseResult<Nothing> {}
