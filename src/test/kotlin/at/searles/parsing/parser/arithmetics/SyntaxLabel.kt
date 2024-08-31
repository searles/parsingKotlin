package at.searles.parsing.parser.arithmetics

enum class SyntaxLabel(val text: String) {
    Plus("+"),
    Minus("-"),
    Times("*"),
    Div("/"),
    Open("("),
    Close(")"),
    Number("<number>")
}