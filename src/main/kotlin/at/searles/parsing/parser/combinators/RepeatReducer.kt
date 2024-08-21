package at.searles.parsing.parser.combinators

import at.searles.parsing.*
import at.searles.parsing.parser.PartialPrintFailure
import at.searles.parsing.parser.PartialPrintResult
import at.searles.parsing.parser.PartialPrintSuccess
import at.searles.parsing.parser.Reducer
import at.searles.parsing.reader.PositionReader

class RepeatReducer<A>(private val reducer: Reducer<A, A>): Reducer<A, A> {
    override fun parse(reader: PositionReader, input: A): ParseResult<A> {
        var value = input
        val start = reader.position
        var end = start

        while(true) {
            when (val result = reducer.parse(reader, value)) {
                is ParseSuccess -> {
                    value = result.value
                    end = result.end
                }
                is ParseFailure -> {
                    return ParseSuccess(value, start, end)
                }
            }
        }
    }

    override fun print(value: A): PartialPrintResult<A> {
        var outputValue = value
        var printOutput = PrintSuccess.empty()

        while(true) {
            when (val result = reducer.print(outputValue)) {
                is PartialPrintSuccess -> {
                    outputValue = result.left
                    printOutput = result.right + printOutput
                }
                is PartialPrintFailure -> return PartialPrintSuccess(outputValue, printOutput)
            }
        }
    }
}