package at.searles.parsing.parser.arithmetics

interface Node {
    fun apply(op: String, vararg otherArgs: Node): Node {
        return Branch(op, this, *otherArgs)
    }

    companion object {
        fun num(n: Int): Node {
            return Num(n)
        }
    }

    class Num(val value: Int) : Node {
        override fun toString(): String {
            return "$value"
        }
    }

    class Branch(val op: String, vararg val args: Node): Node {
        override fun toString(): String {
            return "($op ${args.joinToString(" ")})"
        }
    }
}

