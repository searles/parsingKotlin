package at.searles.parsing.parser.arithmetics

import at.searles.parsing.lexer.Lexer
import at.searles.parsing.lexer.regexp.Ranges
import at.searles.parsing.lexer.regexp.Plus
import at.searles.parsing.lexer.regexp.Text

object SyntaxConsumer {
    val lexer = Lexer<SyntaxLabel>().apply {
        add(Text("+"), SyntaxLabel.Plus)
        add(Text("-"), SyntaxLabel.Minus)
        add(Text("*"), SyntaxLabel.Times)
        add(Text("/"), SyntaxLabel.Div)
        add(Text("("), SyntaxLabel.Open)
        add(Text(")"), SyntaxLabel.Close)
        add(Plus(Ranges('0'.code .. '9'.code)), SyntaxLabel.Number)
    }
}
