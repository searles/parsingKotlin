package at.searles.parsing.parser.combinators

import at.searles.parsing.*
import at.searles.parsing.parser.PartialPrintFailure
import at.searles.parsing.parser.PartialPrintResult
import at.searles.parsing.parser.PartialPrintSuccess
import at.searles.parsing.parser.Reducer
import at.searles.parsing.reader.PositionReader

class RepeatReducer<A>(private val reducer: Reducer<A, A>, private val minCount: Int, private val maxCount: Int?): Reducer<A, A> {
    override fun parse(reader: PositionReader, input: A): ParseResult<A> {
        var value = input
        val start = reader.position
        var end = start

        var count = 0

        while (maxCount == null || count < maxCount) {
            when (val result = reducer.parse(reader, value)) {
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
            return ParseFailure(start, "Expecting at least $minCount $reducer")
        }

        return ParseSuccess(value, start, end)
    }

    override fun print(value: A): PartialPrintResult<A> {
        var outputValue = value
        var printOutput = PrintSuccess.empty()
        var count = 0

        while(maxCount == null || count < maxCount) {
            when (val result = reducer.print(outputValue)) {
                is PartialPrintSuccess -> {
                    count ++
                    outputValue = result.left
                    printOutput = result.right + printOutput
                }
                is PartialPrintFailure -> break
            }
        }

        if (count < minCount) {
            return PartialPrintFailure
        }
        return PartialPrintSuccess(outputValue, printOutput)
    }
}