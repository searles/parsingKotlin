package at.searles.parsing.parser.arithmetics

interface AstNode

class Num(val value: Int) : AstNode

class Branch(val op: SyntaxLabel, args: List<AstNode>): AstNode