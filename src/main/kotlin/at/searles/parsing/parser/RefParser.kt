package at.searles.parsing.parser

import at.searles.parsing.ParseResult
import at.searles.parsing.reader.PositionReader
import kotlin.reflect.KProperty

fun <A, B> ref(initializer: () -> Parser<A, B>): RefParser<A, B> {
    return RefParser(initializer)
}

class RefParser<A, B>(private val initializer: () -> Parser<A, B>): Parser<A, B> {
    private var name: String = "<uninitialized>"
    private var isInitialized = false
    private var innerParser: Parser<A, B>? = null

    operator fun getValue(thisRef: Any?, property: KProperty<*>): Parser<A, B> {
        // Thread safety not required. We assume the initialization is always thread-safe.
        if (!isInitialized) {
            isInitialized = true
            name = "<${property.name}>"
            innerParser = initializer()

            require(innerParser != null)
        }

        return innerParser ?: this
    }

    override fun parse(input: A, reader: PositionReader): ParseResult<B> {
        return innerParser!!.parse(input, reader)
    }

    override fun print(value: B): PrintResult<A> {
        return innerParser!!.print(value)
    }

    override fun toString(): String {
        return name
    }
}

