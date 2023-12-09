fun main() {

    fun List<Int>.toExtrapolation() = generateSequence(this) { it.zipWithNext { a, b -> b - a } }
        .takeWhile { it.any { it != 0 } }
        .sumOf { it.last() }

    fun String.extrapolate() = split(' ').map { it.toInt() }.toExtrapolation()

    fun String.extrapolateBackwards() = split(' ').map { it.toInt() }.reversed().toExtrapolation()

    fun part1(input: List<String>): Int {
        return input.sumOf { it.extrapolate() }
    }

    fun part2(input: List<String>): Int {
        return input.sumOf { it.extrapolateBackwards() }
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day09_test")
    check(part1(testInput) == 114)
    check(part2(testInput) == 2)

    val input = readInput("Day09")
    part1(input).println()
    part2(input).println()
}
