package at.searles.parsing.parser.arithmetics

import at.searles.parsing.InvertFailure
import at.searles.parsing.InvertResult
import at.searles.parsing.InvertSuccess
import at.searles.parsing.lexer.Lexer
import at.searles.parsing.lexer.WithLexer
import at.searles.parsing.lexer.regexp.Plus
import at.searles.parsing.lexer.regexp.Regexp
import at.searles.parsing.parser.*
import at.searles.parsing.parser.Reducer.Companion.rep
import at.searles.parsing.reader.CodePointSequence
import at.searles.parsing.reader.CodePointSequence.Companion.asCodePointSequence

object AstParser: WithLexer {
    override val lexer: Lexer = Lexer()

    private val number = Plus(Regexp.ranges('0'.code .. '9'.code)).parser + num()

    val expr: Parser<Node> = self { sum }

    private val terminal = number or
            "(".recognizer + expr + ")".recognizer

    private val literal = self {
        "-".recognizer + it + convert("-") or
        terminal
    }

    private val prod = literal + (
            "*".recognizer + literal + branch("*") or
                    "/".recognizer + literal + branch("/")
    ).rep()

    private val sum = prod + (
            "+".recognizer + prod + branch("+") or
            "-".recognizer + prod + branch("-")
    ).rep()

    private fun num(): MapAction<CodePointSequence, Node> {
        return object: MapAction<CodePointSequence, Node> {
            override fun convert(value: CodePointSequence): Node {
                return Node.Num(value.toReader().fold(0) { num, digit -> num * 10 + digit - '0'.code })
            }

            override fun invert(result: Node): InvertResult<CodePointSequence> {
                return when (result) {
                    is Node.Num -> InvertSuccess(result.value.toString().asCodePointSequence())
                    else -> InvertFailure
                }
            }
        }
    }

    private fun convert(op: String): MapAction<Node, Node> {
        return object: MapAction<Node, Node> {
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

    private fun branch(op: String): FoldAction<Node, Node, Node> {
        return object: FoldAction<Node, Node, Node> {
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