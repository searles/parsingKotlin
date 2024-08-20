package at.searles.parsing.parser.arithmetics

import at.searles.parsing.Failure
import at.searles.parsing.reader.Consumer
import at.searles.parsing.Result
import at.searles.parsing.Success
import at.searles.parsing.reader.PositionReader

object SyntaxConsumer : Consumer<SyntaxLabel> {
    override fun consume(reader: PositionReader): Result<SyntaxLabel> {
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
            return Failure
        } else {

            return Success(label, checkpoint, reader.position)
        }
    }
}
