package at.searles.parsing.parser

import at.searles.parsing.reader.IndexedReader

class KeywordRecognizer(val keyword: String) : Recognizer {
    override fun recognize(reader: IndexedReader): Boolean {
        val startIndex = reader.index

        val codePoints = keyword.codePoints()
        var isSuccess = true

        codePoints.forEach {
            if (it != reader.read()) {
                reader.index = startIndex
                isSuccess = false
                return@forEach
            }
        }

        return isSuccess
    }
}