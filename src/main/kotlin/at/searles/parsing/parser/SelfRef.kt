package at.searles.parsing.parser

import at.searles.parsing.ParseResult
import at.searles.parsing.reader.PositionReader
import kotlin.reflect.KProperty

fun <A, B> ref(initializer: () -> Parser<A, B>): SelfRefParser<A, B> {
    return SelfRefParser(initializer)
}

abstract class SelfRef<A>(private val initializer: () -> A) {
    private var isInitialized: Boolean = false

    protected var element: A? = null
    protected abstract val default: A

    operator fun getValue(thisRef: Any?, property: KProperty<*>): A {
        if (!isInitialized) {
            isInitialized = true
            element = initializer()
        }

        return element ?: default
    }
}

class SelfRefParser<A, B>(initializer: () -> Parser<A, B>): SelfRef<Parser<A, B>>(initializer), Parser<A, B> {
    override val default: Parser<A, B> get() = this

    override fun parse(input: A, reader: PositionReader): ParseResult<B> {
        return element?.parse(input, reader) ?: error("Not initialized")
    }

    override fun print(value: B): PrintResult<A> {
        return element?.print(value) ?: error("Not initialized")
    }
}

