package at.searles.parsing.lexer

import at.searles.parsing.ParseFailure
import at.searles.parsing.ParseResult
import at.searles.parsing.ParseSuccess
import at.searles.parsing.reader.CodePointSequence.Companion.asCodePointSequence
import at.searles.parsing.reader.Consumer
import at.searles.parsing.reader.PositionReader
import at.searles.parsing.utils.IntRangeMap

class Node(private val name: String) {
    var labels: Set<Label>? = null
    var edges: IntRangeMap<Node> = IntRangeMap()

    fun connectTo(values: IntRange, target: Node) {
        edges.add(values, target) { _, _ -> error("No overlaps allowed in DFA") }
    }

    override fun toString(): String {
        return name
    }
}

class Automaton(private val startNode: Node, private val finalNodes: Set<Node>): Consumer<Set<Label>> {
    init {
        val nodes = collectNodes(startNode)
        require(finalNodes.all { it in nodes })
    }

    override fun consume(reader: PositionReader): ParseResult<Set<Label>> {
        val start = reader.position
        var mark = -1L
        var labels: Set<Label>? = null

        var node: Node? = startNode

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

    fun applyLabel(label: Label): Automaton {
        finalNodes.forEach {
            require(it.labels == null)
            it.labels = setOf(label)
        }

        return this
    }

    fun findEquivalentLabel(label: Label): Label? {
        val distinctLabels = finalNodes
            .asSequence()
            .mapNotNull { it.labels }
            .filter { label !in it }
            .flatten()
            .toSet()

        return finalNodes
            .asSequence()
            .mapNotNull { it.labels }
            .filter { label in it }
            .reduce { acc, set -> acc.intersect(set) }
            .minus(distinctLabels)
            .firstOrNull { it != label }
    }

    fun removeLabel(label: Label) {
        finalNodes.filter { label in it.labels!! }.forEach {
            it.labels = it.labels!! - setOf(label)
        }
    }

    fun or(other: Automaton): Automaton {
        return DfaFactory(
            startNodes = setOf(startNode, other.startNode),
            finalNodes = finalNodes + other.finalNodes,
            epsilonEdges = emptyMap()
        ).create()
    }

    fun then(other: Automaton): Automaton {
        return DfaFactory(
            startNodes = setOf(startNode),
            finalNodes = other.finalNodes,
            epsilonEdges = finalNodes.associateWith { setOf(other.startNode) }
        ).create()
    }

    fun plus(): Automaton {
        return DfaFactory(
            startNodes = setOf(startNode),
            finalNodes = finalNodes,
            epsilonEdges = finalNodes.associateWith { setOf(startNode) }
        ).create()
    }

    fun opt(): Automaton {
        return DfaFactory(
            startNodes = setOf(startNode),
            finalNodes = finalNodes + startNode,
            epsilonEdges = emptyMap()
        ).create()
    }

    companion object {
        fun nothing(): Automaton {
            val node = Node("q0")
            return Automaton(node, emptySet())
        }

        fun ofRange(vararg values: IntRange): Automaton {
            val q0 = Node("q0")
            val q1 = Node("q1")

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

        fun ofString(string: String): Automaton {
            var count = 0

            var node = Node("q0")

            string.asCodePointSequence().toReader().fold(node) {
                n, cp -> run {
                    node = Node("q${count++}")
                    n.connectTo(cp .. cp, node)
                    node
                }
            }

            return Automaton(node, setOf(node))
        }

        private fun collectNodes(startNode: Node): Set<Node> {
            val processingQueue = mutableListOf(startNode)
            val nodes = mutableSetOf<Node>()

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

class DfaFactory(startNodes: Set<Node>, private val finalNodes: Set<Node>, private val epsilonEdges: Map<Node, Set<Node>>) {
    private val startNodeSet = withEpsilonTransitions(startNodes)
    private val nodeProcessingQueue = mutableListOf<Set<Node>>()
    private val connections = mutableMapOf<Set<Node>, IntRangeMap<Set<Node>>>()

    fun create(): Automaton {
        nodeProcessingQueue.add(startNodeSet)

        while (nodeProcessingQueue.isNotEmpty()) {
            processNodeSet(nodeProcessingQueue.removeLast())
        }

        // Automaton is potentially not minimal. Common minimization techniques
        // are not applicable though because the finalNodes also contain labels.
        return generateAutomaton()
    }

    private fun generateAutomaton(): Automaton {
        var count = 0
        val newNodes = connections.keys.associateWith { nodeSet ->
            Node("q${count++}").apply {
                labels = getLabels(nodeSet)
            }
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

    private fun getLabels(nodes: Set<Node>): Set<Label>? {
        if (nodes.any { it in finalNodes && it.labels?.isNotEmpty() == true }) {
            return nodes.flatMap { it.labels ?: emptyList() }.toSet()
        }

        return null
    }

    private fun processNodeSet(nodeSet: Set<Node>) {
        // Contract: nodeSet is closed under epsilon connections
        if (connections.containsKey(nodeSet)) {
            return
        }

        val edges = getEdges(nodeSet)
        connections[nodeSet] = edges
        nodeProcessingQueue.addAll(edges.values)
    }

    private fun getEdges(nodeSet: Set<Node>): IntRangeMap<Set<Node>> {
        val newEdges = IntRangeMap<Set<Node>>()

        for (node in nodeSet) {
            newEdges.add(node.edges.mapValues { setOf(it) }) { set0, set1 -> set0 + set1 }
        }

        return newEdges.mapValues { withEpsilonTransitions(it) }
    }

    private fun withEpsilonTransitions(nodeSet: Set<Node>): Set<Node> {
        val nodeList = mutableListOf<Node>().apply { addAll(nodeSet) }
        val closure = mutableSetOf<Node>()

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
