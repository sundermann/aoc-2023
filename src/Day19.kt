fun main() {
    data class Rating(val x: Int, val m: Int, val a: Int, val s: Int) {
        fun get(char: Char) = when(char) {
                'x' -> x
                'm' -> m
                'a' -> a
                's' -> s
                else -> error("input error")
        }
    }
    data class Rule(val rule: String) {
        val condition get(): String? {
            if (rule.any { it == ':' }) {
                return rule.split(':').first()
            }
            return null
        }

        val selector get() = rule.first()
        val threshold get() = rule.split('<', '>', ':')[1].toInt()
        val destination get() = rule.split(':').last()
        val greaterThan = rule.any { it == '>' }

        fun matches(rating: Rating): Boolean {
            condition?.let {
                val value = rating.get(it.first())
                return if (greaterThan) value > threshold else value < threshold
            }
            return true
        }

        fun split(ratingRange: Map<Char, IntRange>): Pair<Map<Char, IntRange>?, Map<Char, IntRange>?> {
            val range = ratingRange[selector]!!
            val failing = if (greaterThan) range.first..threshold else threshold..range.last
            val passing = if (greaterThan) threshold + 1..range.last else range.first..< threshold

            val passingRange = passing.takeUnless(IntRange::isEmpty)?.let {
                val new = ratingRange.toMutableMap()
                new[selector] = it
                new
            }
            val failingRange = failing.takeUnless(IntRange::isEmpty)?.let {
                val new = ratingRange.toMutableMap()
                new[selector] = it
                new
            }

            return passingRange to failingRange
        }
    }
    data class Workflow(val name: String, val rules: List<Rule>, val fallback: String) {
        fun parse(rating: Rating) = rules.find { it.matches(rating) }?.destination ?: fallback
    }

    fun String.toWorkflow(): Workflow {
        val (name, rules) = dropLast(1).split('{')
        return Workflow(name, rules.split(',').dropLast(1).map { Rule(it) }, rules.split(',').last())
    }

    fun String.toRating() = split('=', ',', '}').let { Rating(it[1].toInt(), it[3].toInt(), it[5].toInt(), it[7].toInt()) }

    fun part1(input: List<String>): Int {
        val workflows = input.takeWhile { it.isNotBlank() }.map { it.toWorkflow() }.associateBy { it.name }
        val ratings = input.dropWhile { it.isNotBlank() }.drop(1).map { it.toRating() }

        return ratings.filter {
            var current = workflows["in"]!!
            var result = current.parse(it)
            while(result != "A" && result != "R") {
                current = workflows[result]!!
                result = current.parse(it)
            }
            result == "A"
        }.sumOf { it.x + it.m + it.a + it.s }
    }

    fun part2(input: List<String>): Long {
        val workflows = input.takeWhile { it.isNotBlank() }.map { it.toWorkflow() }.associateBy { it.name }

        val passingRanges = buildList {
            fun filterRange(workflowName: String, ranges: Map<Char, IntRange>) {
                if (workflowName == "A") {
                    add(ranges)
                    return
                }
                if (workflowName == "R") return

                val workflow = workflows[workflowName]!!
                val fallbackRange = workflow.rules.fold(ranges) { range, rule ->
                    val (success, failure) = rule.split(range)
                    if (success != null) filterRange(rule.destination, success)
                    failure ?: return
                }

                filterRange(workflow.fallback, fallbackRange)
            }

            filterRange("in", listOf('x', 'm', 'a', 's').associateWith { 1..4000 })
        }

        return passingRanges
            .sumOf { it.values.map { if (it.isEmpty()) 0 else it.last - it.first + 1L }.reduce(Long::times) }
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day19_test")
    check(part1(testInput) == 19114)
    check(part2(testInput) == 167409079868000L)

    val input = readInput("Day19")
    part1(input).println()
    part2(input).println()
}