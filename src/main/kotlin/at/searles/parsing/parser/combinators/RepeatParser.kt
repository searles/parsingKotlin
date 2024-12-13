package at.searles.parsing.parser.combinators

import at.searles.parsing.*
import at.searles.parsing.parser.*
import at.searles.parsing.reader.PositionReader

class RepeatParser<A>(private val parser: Parser<A, A>, private val minCount: Int, private val maxCount: Int?): Parser<A, A> {
    override fun parse(input: A, reader: PositionReader): ParseResult<A> {
        var value = input
        val start = reader.position
        var end = start

        var count = 0

        while (maxCount == null || count < maxCount) {
            when (val result = parser.parse(value, reader)) {
                is ParseSuccess -> {
                    count ++
                    value = result.value
                    end = result.end
                }
                is ParseFailure -> break
            }
        }

        if (count < minCount) {
            reader.position = start
            return ParseFailure(start, "Expecting at least $minCount $parser")
        }

        return ParseSuccess(value, start, end)
    }

    override fun print(value: A): PrintResult<A> {
        var result = PrintSuccess(value, OutputTree.empty())
        var count = 0

        while(maxCount == null || count < maxCount) {
            when (val r = parser.print(result.value)) {
                is PrintSuccess -> {
                    count ++
                    result = PrintSuccess(r.value, r.output + result.output)
                }
                is PrintFailure -> break
            }
        }

        if (count < minCount) {
            return PrintFailure
        }
        return result
    }

    override fun toString(): String {
        return "repeat[$minCount, $maxCount]($parser)"
    }
}