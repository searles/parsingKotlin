package at.searles.parsing.parser

import at.searles.parsing.PrintSuccess

sealed interface PartialPrintResult<out A>
data class PartialPrintSuccess<out A>(val left: A, val right: PrintSuccess): PartialPrintResult<A>
data object PartialPrintFailure: PartialPrintResult<Nothing>

