private data class Signal(val source: String, val pulse: Boolean, val destination: String)

private sealed interface Module {
    val name: String
    val inputs: List<String>
    val outputs: List<String>

    fun process(signal: Signal): Boolean?
}

private fun Module.processAndReturn(signal: Signal): List<Signal> {
    val output = process(signal) ?: return emptyList()
    return outputs.map { Signal(name, output, it) }
}

private data class FlipFlop(override val name: String, override val inputs: List<String>, override val outputs: List<String>) : Module {
    private var state: Boolean = false
    override fun process(signal: Signal): Boolean? {
        if (signal.pulse) return null
        state = !state
        return state
    }
}

private data class Conjunction(override val name: String, override val inputs: List<String>, override val outputs: List<String>) : Module {
    val state: MutableMap<String, Boolean> = inputs.associateWith { false }.toMutableMap()
    override fun process(signal: Signal): Boolean {
        state[signal.source] = signal.pulse
        return !state.values.all { it }
    }
}

private data class Broadcast(override val name: String, override val inputs: List<String>, override val outputs: List<String>) : Module {
    override fun process(signal: Signal): Boolean = signal.pulse
}

private data class End(override val name: String, override val inputs: List<String>, override val outputs: List<String>) : Module {
    override fun process(signal: Signal) = null
}

fun main() {

    fun String.toModule(): Triple<String, String, List<String>> {
        val type = if (first() in listOf('%', '&')) first().toString() else ""
        val (name, outputs) = trimStart('%', '&').split(" -> ", "%", "&")
        return Triple(type, name, outputs.split(", ").filter { it.isNotEmpty() })
    }

    fun parse(input: List<String>): Map<String, Module> {
        val inputs = mutableMapOf<String, MutableList<String>>()
        val outputs = mutableMapOf<String, List<String>>()
        val names = mutableSetOf<String>()
        val types = mutableMapOf<String, String>()

        input.map { it.toModule() }.forEach { (type, name, modules) ->
            modules.forEach { inputs.getOrPut(it) { mutableListOf() }.add(name) }
            outputs[name] = modules
            types[name] = type
            names += name
            names += modules
        }

        return names.map { module ->
                val inModules = inputs[module] ?: emptyList()
                val outModules = outputs[module] ?: emptyList()
                when (types[module]) {
                    "" -> Broadcast(module, inModules, outModules)
                    "%" -> FlipFlop(module, inModules, outModules)
                    "&" -> Conjunction(module, inModules, outModules)
                    null -> End(module, inModules, emptyList())
                    else -> error("")
                }
            }
            .associateBy { it.name }
    }

    fun part1(input: List<String>): Long {
        val modules = parse(input)

        var low = 0L
        var high = 0L

        var simulations = 0
        fun simulate() = sequence {
            val queue = MutableList(1) { Signal("", false, "broadcaster") }

            simulations++
            while (queue.isNotEmpty()) {
                val signal = queue.removeFirst()
                yield(signal)
                modules[signal.destination]!!.processAndReturn(signal).let { queue.addAll(it) }
            }
        }

        repeat(1000) { simulate().forEach { if (it.pulse) high++ else low++ } }
        return low * high
    }

    fun gcd(a: Long, b: Long): Long = if (a == 0L) b else gcd(b % a, a)
    fun lcm(a: Long, b: Long): Long = (a * b) / gcd(a, b)
    fun MutableCollection<Long>.lcm() = reduce { a, b -> lcm(a, b) }

    fun part2(input: List<String>): Long {
        val modules = parse(input)
        var simulations = 0L

        fun simulate() = sequence {
            val queue = MutableList(1) { Signal("", false, "broadcaster") }

            simulations++
            while (queue.isNotEmpty()) {
                val signal = queue.removeFirst()
                yield(signal)
                modules[signal.destination]!!.processAndReturn(signal).let { queue.addAll(it) }
            }
        }

        val rxModule = modules["rx"]!!
        val rxInput = rxModule.inputs.first().let { modules[it]!! }

        val cycles: MutableMap<String, Long> = rxInput.inputs.associateWith { -1L }.toMutableMap()
        while (cycles.values.any { it == -1L }) {
            simulate()
                .filter { s -> s.destination == rxInput.name && s.pulse && cycles[s.source] == -1L }
                .forEach { s -> cycles[s.source] = simulations }
        }
        return cycles.values.lcm()
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day20_test")
    check(part1(testInput) == 11687500L)

    val input = readInput("Day20")
    part1(input).println()
    part2(input).println()
}