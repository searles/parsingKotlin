package at.searles.parsing.lexer

@JvmInline
value class Label(val value: Int) {
    fun next(): Label = Label(this.value + 1)
}

class LabelStream(private var label: Label = Label(0)): Iterator<Label> {
    override fun hasNext(): Boolean = true

    override fun next(): Label {
        label = label.next()
        return label
    }

}