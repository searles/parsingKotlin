package at.searles.parsing

import at.searles.parsing.reader.CodePointSequence

interface OutputTree {
    operator fun plus(right: OutputTree): OutputTree {
        return ComposedOutputTree(this, right)
    }

    companion object {
        fun empty(): OutputTree {
            return EmptyOutputTree
        }
    }
}

data object EmptyOutputTree : OutputTree {
    override fun plus(right: OutputTree): OutputTree {
        return right
    }

    override fun toString(): String {
        return ""
    }
}

data class TextOutputTree(val codePointSequence: CodePointSequence) : OutputTree {
    override fun toString(): String {
        return codePointSequence.toString()
    }
}

data class ComposedOutputTree(val left: OutputTree, val right: OutputTree) : OutputTree {
    override fun toString(): String {
        return "$left$right"
    }
}


