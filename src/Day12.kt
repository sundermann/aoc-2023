fun main() {

    val cache = mutableMapOf<Triple<String, List<Int>, Int>, Long>()
    fun String.groups(groups: List<Int>, handledChars: Int = 0): Long {
        if (isEmpty()) return if (groups.isEmpty() && handledChars == 0) 1 else 0

        return cache.getOrPut(Triple(this, groups, handledChars)) {
            val current = first()
            val remaining = drop(1)

            var solutions = 0L
            val possible = if (current == '?') listOf('.', '#') else listOf(current)
            possible.forEach { c ->
                if (c == '#')
                    solutions += remaining.groups(groups, handledChars + 1)
                if (c == '.') {
                    if (handledChars > 0) {
                        if (groups.isNotEmpty() && groups.first() == handledChars)
                            solutions += remaining.groups(groups.drop(1))
                    } else {
                        solutions += remaining.groups(groups)
                    }
                }
            }
            solutions
        }
    }

    fun String.arrangements(): Long {
        val (conditions, record) = split(' ')
        val records = record.split(',').map { it.toInt() }

        return "$conditions.".groups(records)
    }

    fun String.unfold(): String {
        val (conditions, record) = split(' ')
        return "${List(5) {conditions}.joinToString("?")}. ${List(5) {record}.joinToString(",")}"
    }

    fun part1(input: List<String>): Long {
        return input.sumOf { it.arrangements() }
    }

    fun part2(input: List<String>): Long {
        return input.map { it.unfold() }.sumOf { it.arrangements() }
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day12_test")
    check(part1(testInput) == 21L)
    check(part2(testInput) == 525152L)

    val input = readInput("Day12")
    part1(input).println()
    part2(input).println()
}
