package at.searles.parsing.parser

import at.searles.parsing.OutputTree

sealed interface PrintResult<out A>
data class PrintSuccess<out A>(val value: A, val output: OutputTree): PrintResult<A>
data object PrintFailure: PrintResult<Nothing>

