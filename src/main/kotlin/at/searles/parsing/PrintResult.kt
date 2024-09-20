package at.searles.parsing

import at.searles.parsing.parser.PartialPrintSuccess
import at.searles.parsing.reader.CodePointSequence

sealed interface PrintResult {
    operator fun plus(right: PrintSuccess): PrintSuccess
}

data object PrintFailure: PrintResult {
    override fun plus(right: PrintSuccess): PrintSuccess {
        error("Cannot compose failure")
    }
}

interface PrintSuccess : PrintResult {
    override fun plus(right: PrintSuccess): PrintSuccess {
        return ComposedPrintSuccess(this, right)
    }

    companion object {
        fun empty(): PrintSuccess {
            return EmptyPrintSuccess
        }
    }
}

data object EmptyPrintSuccess : PrintSuccess {
    override fun plus(right: PrintSuccess): PrintSuccess {
        return right
    }

    override fun toString(): String {
        return ""
    }
}

data class TextPrintSuccess(val codePointSequence: CodePointSequence) : PrintSuccess {
    override fun toString(): String {
        return codePointSequence.toString()
    }
}

data class ComposedPrintSuccess(val left: PrintSuccess, val right: PrintSuccess) : PrintSuccess {
    override fun toString(): String {
        return "$left$right"
    }
}


