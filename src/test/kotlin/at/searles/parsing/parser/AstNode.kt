package at.searles.parsing.parser

interface AstNode

class IntNode(val value: Int) : AstNode

class Branch(val op: String, args: List<AstNode>): AstNode