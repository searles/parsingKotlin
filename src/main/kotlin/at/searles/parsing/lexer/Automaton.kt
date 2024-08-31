package at.searles.parsing.lexer

import at.searles.parsing.ParseFailure
import at.searles.parsing.ParseResult
import at.searles.parsing.ParseSuccess
import at.searles.parsing.reader.Consumer
import at.searles.parsing.reader.PositionReader
import at.searles.parsing.utils.IntRangeMap

class Node<A>(
    var labels: Set<A>? = null,
    var edges: IntRangeMap<Node<A>> = IntRangeMap()
) {
    fun connectTo(values: IntRange, target: Node<A>) {
        edges.add(values, target) { _, _ -> error("No overlaps allowed in DFA") }
    }
}

class Automaton<A>(private val startNode: Node<A>, private val finalNodes: Set<Node<A>>): Consumer<Set<A>> {
    init {
        val nodes = collectNodes(startNode)
        require(finalNodes.all { it in nodes })
        require(nodes.all { it.labels == null && it !in finalNodes || it.labels != null && it in finalNodes })
    }

    override fun consume(reader: PositionReader): ParseResult<Set<A>> {
        val start = reader.position
        var mark = -1L
        var labels: Set<A>? = null

        var node: Node<A>? = startNode

        while (node != null) {
            if (node.labels != null) {
                mark = reader.position
                labels = node.labels!!
            }

            val cp = reader.read()
            node = node.edges[cp]
        }

        if (labels != null) {
            assert(mark != -1L)
            reader.position = mark

            return ParseSuccess(labels, start, mark)
        } else {
            reader.position = start
            return ParseFailure(start, "Unexpected char")
        }
    }

    fun applyLabel(labels: A): Automaton<A> {
        finalNodes.forEach {
            require(it.labels!!.isEmpty())
            it.labels = setOf(labels)
        }

        return this
    }

    fun or(other: Automaton<A>): Automaton<A> {
        return DfaFactory(
            startNodes = setOf(startNode, other.startNode),
            finalNodes = finalNodes + other.finalNodes,
            epsilonEdges = emptyMap()
        ).create()
    }

    fun then(other: Automaton<A>): Automaton<A> {
        return DfaFactory(
            startNodes = setOf(startNode),
            finalNodes = other.finalNodes,
            epsilonEdges = finalNodes.associateWith { setOf(other.startNode) }
        ).create()
    }

    fun plus(): Automaton<A> {
        return DfaFactory(
            startNodes = setOf(startNode),
            finalNodes = finalNodes,
            epsilonEdges = finalNodes.associateWith { setOf(startNode) }
        ).create()
    }

    fun opt(): Automaton<A> {
        return DfaFactory(
            startNodes = setOf(startNode),
            finalNodes = finalNodes + startNode,
            epsilonEdges = emptyMap()
        ).create()
    }

    companion object {
        fun <A> nothing(): Automaton<A> {
            val node = Node<A>()
            return Automaton(node, emptySet())
        }

        fun <A> ofRange(vararg values: IntRange): Automaton<A> {
            val q0 = Node<A>()
            val q1 = Node<A>(labels = emptySet())

            withoutOverlaps(values).forEach {
                q0.connectTo(it, q1)
            }

            return Automaton(q0, setOf(q1))
        }

        private fun withoutOverlaps(values: Array<out IntRange>): List<IntRange> {
            if (values.isEmpty()) return emptyList()

            val result = mutableListOf<IntRange>()

            values.sortedBy { it.first }.forEach { range ->
                if (result.isEmpty() || result.last().last < range.first - 1) {
                    result.add(range)
                } else {
                    val other = result.removeLast()
                    result.add(other.first..maxOf(range.last, other.last))
                }
            }

            return result
        }

        fun <A> ofString(string: String): Automaton<A> {
            val finalNode = Node<A>(emptySet())
            var node = finalNode

            string.reversed().codePoints().forEach { codePoint ->
                node = Node<A>().apply {
                    connectTo(codePoint .. codePoint, node)
                }
            }

            return Automaton(node, setOf(finalNode))
        }

        private fun <A> collectNodes(startNode: Node<A>): Set<Node<A>> {
            val processingQueue = mutableListOf(startNode)
            val nodes = mutableSetOf<Node<A>>()

            while (processingQueue.isNotEmpty()) {
                val node = processingQueue.removeLast()
                if (node !in nodes) {
                    processingQueue.addAll(node.edges.values)
                    nodes.add(node)
                }
            }

            return nodes
        }
    }
}

class DfaFactory<A>(startNodes: Set<Node<A>>, private val finalNodes: Set<Node<A>>, private val epsilonEdges: Map<Node<A>, Set<Node<A>>>) {
    private val startNodeSet = withEpsilonTransitions(startNodes)
    private val nodeProcessingQueue = mutableListOf<Set<Node<A>>>()
    private val connections = mutableMapOf<Set<Node<A>>, IntRangeMap<Set<Node<A>>>>()

    fun create(): Automaton<A> {
        nodeProcessingQueue.add(startNodeSet)

        while (nodeProcessingQueue.isNotEmpty()) {
            processNodeSet(nodeProcessingQueue.removeLast())
        }

        // Automaton is potentially not minimal. Common minimization techniques
        // are not applicable though because the finalNodes also contain labels.
        return generateAutomaton()
    }

    private fun generateAutomaton(): Automaton<A> {
        val newNodes = connections.keys.associateWith { nodeSet ->
            Node(getLabels(nodeSet))
        }

        for (edge in connections) {
            with(newNodes.getValue(edge.key)) {
                edges = edge.value.mapValues { newNodes.getValue(it) }
            }
        }

        val startNode = newNodes.getValue(startNodeSet)
        val finalNodes = newNodes.filter { entry -> entry.key.any { it in finalNodes } }.values.toSet()

        return Automaton(startNode, finalNodes)
    }

    private fun getLabels(nodes: Set<Node<A>>): Set<A>? {
        if (nodes.any { it in finalNodes }) {
            return nodes.flatMap { it.labels ?: emptyList() }.toSet()
        }

        return null
    }

    private fun processNodeSet(nodeSet: Set<Node<A>>) {
        // Contract: nodeSet is closed under epsilon connections
        if (connections.containsKey(nodeSet)) {
            return
        }

        val edges = getEdges(nodeSet)
        connections[nodeSet] = edges
        nodeProcessingQueue.addAll(edges.values)
    }

    private fun getEdges(nodeSet: Set<Node<A>>): IntRangeMap<Set<Node<A>>> {
        val newEdges = IntRangeMap<Set<Node<A>>>()

        for (node in nodeSet) {
            newEdges.add(node.edges.mapValues { setOf(it) }) { set0, set1 -> set0 + set1 }
        }

        return newEdges.mapValues { withEpsilonTransitions(it) }
    }

    private fun withEpsilonTransitions(nodeSet: Set<Node<A>>): Set<Node<A>> {
        val nodeList = mutableListOf<Node<A>>().apply { addAll(nodeSet) }
        val closure = mutableSetOf<Node<A>>()

        while (nodeList.isNotEmpty()) {
            val node = nodeList.removeLast()

            if (closure.contains(node)) {
                continue
            }

            closure.add(node)

            epsilonEdges[node]?.let {
                nodeList.addAll(it)
            }
        }

        return closure
    }
}
