package at.searles.parsing.parser.arithmetics

import at.searles.parsing.*
import at.searles.parsing.reader.Consumer
import at.searles.parsing.reader.CodePointSequence
import at.searles.parsing.reader.PositionReader

object SyntaxConsumer : Consumer<SyntaxLabel> {
    override fun consume(reader: PositionReader): ParseResult<SyntaxLabel> {
        val checkpoint = reader.position
        var cp = reader.read()

        val label: SyntaxLabel? = when (cp) {
            '+'.code -> SyntaxLabel.Plus
            '-'.code -> SyntaxLabel.Minus
            '*'.code -> SyntaxLabel.Times
            '/'.code -> SyntaxLabel.Div
            '('.code -> SyntaxLabel.Open
            ')'.code -> SyntaxLabel.Close
            in '0'.code..'9'.code -> {
                while (true) {
                    val mark = reader.position
                    cp = reader.read()
                    if (cp !in '0'.code .. '9'.code) {
                        reader.position = mark
                        break
                    }
                }

                SyntaxLabel.Number
            }
            else -> null
        }

        if (label == null) {
            reader.position = checkpoint
            return ParseFailure(checkpoint, "Unknown label")
        } else {

            return ParseSuccess(label, checkpoint, reader.position)
        }
    }

    override fun print(label: SyntaxLabel, sequence: CodePointSequence): PrintResult {
        return when (label) {
            SyntaxLabel.Number -> StringPrintSuccess(sequence.toString())
            else -> PrintFailure
        }
    }

    override fun print(label: SyntaxLabel): PrintResult {
        return when (label) {
            SyntaxLabel.Plus -> StringPrintSuccess("+")
            SyntaxLabel.Minus -> StringPrintSuccess("-")
            SyntaxLabel.Times -> StringPrintSuccess("*")
            SyntaxLabel.Div -> StringPrintSuccess("/")
            SyntaxLabel.Open -> StringPrintSuccess("(")
            SyntaxLabel.Close -> StringPrintSuccess(")")
            else -> PrintFailure
        }
    }
}
