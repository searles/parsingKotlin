package at.searles.parsing

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

data class StringPrintSuccess(val string: String) : PrintSuccess {
    override fun toString(): String {
        return string
    }
}

data class ComposedPrintSuccess(val left: PrintSuccess, val right: PrintSuccess) : PrintSuccess {
    override fun toString(): String {
        return "$left$right"
    }
}


