package at.searles.parsing.parser

interface ParserResult<A> {
    val isSuccess: Boolean
    val value: A

    companion object {
        fun <A> failure(): ParserResult<A> {
            return object: ParserResult<A> {
                override val isSuccess: Boolean
                    get() = false
                override val value: A
                    get() = error("Failure")

                override fun toString(): String = "failure"
            }
        }

        fun <A> success(value: A): ParserResult<A> {
            return object: ParserResult<A> {
                override val isSuccess: Boolean
                    get() = true
                override val value: A
                    get() = value

                override fun toString(): String = "success($value)"
            }
        }
    }
}