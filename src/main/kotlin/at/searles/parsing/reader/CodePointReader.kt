package at.searles.parsing.reader

interface CodePointReader {
    fun read(): Int
    fun <A> fold(init: A, aggregate: (A, Int) -> A): A {
        var result = init

        while (true) {
            val cp = read()

            if (cp == -1) {
                return result
            }

            result = aggregate(result, cp)
        }
    }
}