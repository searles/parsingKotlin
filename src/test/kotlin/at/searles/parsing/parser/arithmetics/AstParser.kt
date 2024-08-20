package at.searles.parsing.parser.arithmetics

import at.searles.parsing.parser.*
import at.searles.parsing.parser.Reducer.Companion.rep
import at.searles.parsing.reader.CodePointReader

object AstParser {
    private val number = ConsumerParser(SyntaxConsumer, SyntaxLabel.Number) {
        Node.num(MathParser.parseNumber(it))
    }

    val expr: Parser<Node> = self { sum }

    private val terminal = number or
            MathParser.kw(SyntaxLabel.Open) + expr + MathParser.kw(SyntaxLabel.Close)

    private val literal = self {
        MathParser.kw(SyntaxLabel.Minus) + it + Converter { arg: Node -> arg.apply("~") } or
        terminal
    }

    private val prod = literal + (
            MathParser.kw(SyntaxLabel.Times) + literal + Fold { left: Node, right: Node -> left.apply("*", right) } or
            MathParser.kw(SyntaxLabel.Div) + literal + Fold { left: Node, right: Node -> left.apply("/", right) }
    ).rep()

    private val sum = prod + (
            MathParser.kw(SyntaxLabel.Plus) + prod + Fold { left: Node, right: Node -> left.apply("+", right) } or
            MathParser.kw(SyntaxLabel.Minus) + prod + Fold { left: Node, right: Node -> left.apply("-", right) }
    ).rep()
}