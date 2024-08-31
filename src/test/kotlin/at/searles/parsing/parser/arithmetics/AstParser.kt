package at.searles.parsing.parser.arithmetics

import at.searles.parsing.InvertFailure
import at.searles.parsing.InvertResult
import at.searles.parsing.InvertSuccess
import at.searles.parsing.parser.*
import at.searles.parsing.parser.Reducer.Companion.rep
import at.searles.parsing.reader.CodePointSequence
import at.searles.parsing.reader.CodePointSequence.Companion.asCodePointSequence

object AstParser {
    private val number = LexParser(SyntaxLabel.Number, SyntaxConsumer.lexer) + num()

    val expr: Parser<Node> = self { sum }

    private val terminal = number or
            MathParser.kw(SyntaxLabel.Open) + expr + MathParser.kw(SyntaxLabel.Close)

    private val literal = self {
        MathParser.kw(SyntaxLabel.Minus) + it + convert("-") or
        terminal
    }

    private val prod = literal + (
            MathParser.kw(SyntaxLabel.Times) + literal + branch("*") or
            MathParser.kw(SyntaxLabel.Div) + literal + branch("/")
    ).rep()

    private val sum = prod + (
            MathParser.kw(SyntaxLabel.Plus) + prod + branch("+") or
            MathParser.kw(SyntaxLabel.Minus) + prod + branch("-")
    ).rep()

    private fun num(): Converter<CodePointSequence, Node> {
        return object: Converter<CodePointSequence, Node> {
            override fun convert(value: CodePointSequence): Node {
                return Node.Num(MathParser.num(value))
            }

            override fun invert(result: Node): InvertResult<CodePointSequence> {
                return when (result) {
                    is Node.Num -> InvertSuccess(result.value.toString().asCodePointSequence())
                    else -> InvertFailure
                }
            }
        }
    }

    private fun convert(op: String): Converter<Node, Node> {
        return object: Converter<Node, Node> {
            override fun convert(value: Node): Node {
                return value.apply(op)
            }

            override fun invert(result: Node): InvertResult<Node> {
                return when (result) {
                    is Node.Branch -> when {
                        result.op == op && result.args.size == 1 -> InvertSuccess(result.args[0])
                        else -> InvertFailure
                    }
                    else -> InvertFailure
                }
            }
        }
    }

    private fun branch(op: String): Fold<Node, Node, Node> {
        return object: Fold<Node, Node, Node> {
            override fun fold(left: Node, right: Node): Node {
                return left.apply(op, right)
            }

            override fun leftInverse(result: Node): InvertResult<Node> {
                return when (result) {
                    is Node.Branch -> when {
                        result.op == op && result.args.size == 2 -> InvertSuccess(result.args[0])
                        else -> InvertFailure
                    }
                    else -> InvertFailure
                }
            }

            override fun rightInverse(result: Node): InvertResult<Node> {
                return when (result) {
                    is Node.Branch -> when {
                        result.op == op && result.args.size == 2 -> InvertSuccess(result.args[1])
                        else -> InvertFailure
                    }
                    else -> InvertFailure
                }
            }
        }
    }
}