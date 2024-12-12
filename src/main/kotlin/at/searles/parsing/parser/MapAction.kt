package at.searles.parsing.parser

import at.searles.parsing.*
import at.searles.parsing.reader.PositionReader

// By using A == Unit or B == Unit, this becomes an Init or Consume.
fun interface MapAction<A, B>: Parser<A, B> {
    fun convert(value: A): B
    fun invert(result: B): InvertResult<A> {
        error("No inverse defined")
    }

    override fun parse(input: A, reader: PositionReader): ParseResult<B> {
        return ParseSuccess(convert(input), reader.position, reader.position)
    }

    override fun print(value: B): PrintResult<A> {
        return when (val result = invert(value)) {
            is InvertSuccess -> PrintSuccess(result.value, EmptyOutputTree)
            InvertFailure  -> PrintFailure
        }
    }

    companion object {
        fun <A> init(initFn: () -> A): MapAction<Unit, A> {
            return object : MapAction<Unit, A> {
                override fun convert(value: Unit): A {
                    return initFn()
                }

                override fun invert(result: A): InvertResult<Unit> {
                    return InvertSuccess(Unit)
                }
            }
        }

        fun <A> elim(elimFn: () -> A): MapAction<A, Unit> {
            return object : MapAction<A, Unit> {
                override fun convert(value: A) {
                    return
                }

                override fun invert(result: Unit): InvertResult<A> {
                    return InvertSuccess(elimFn())
                }
            }
        }
    }
}
