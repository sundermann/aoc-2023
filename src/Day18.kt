import kotlin.math.absoluteValue

enum class DigDirection(val point: Pair<Int, Int>) { RIGHT(0 to 1), DOWN(1 to 0), LEFT(0 to -1), UP(-1 to 0) }

fun main() {
    data class Instruction(val direction: DigDirection, val steps: Int, val color: String)

    operator fun Pair<Int, Int>.plus(other: Pair<Int, Int>) = first + other.first to second + other.second
    operator fun Pair<Int, Int>.times(scalar: Int) = first * scalar to second * scalar

    fun List<Pair<DigDirection, Int>>.area() =
        runningFold(0 to 0) { point, (dir, steps) -> point + dir.point * steps }
            .zipWithNext { (y1, x1), (y2, x2) -> (x2 - x1).toLong() * y1 }
            .sum().absoluteValue + sumOf { (dir, steps) -> steps } / 2 + 1

    fun String.toDirection() = when(first()) {
        'R' -> DigDirection.RIGHT
        'L' -> DigDirection.LEFT
        'U' -> DigDirection.UP
        'D' -> DigDirection.DOWN
        else -> error("No direction")
    }

    fun String.toInstruction(): Instruction {
        val (direction, meters, color) = split(' ')
        return Instruction(direction.toDirection(), meters.toInt(), color.substring(2..7))
    }

    fun part1(input: List<String>): Long {
        return input.map { line -> line.toInstruction().let { it.direction to it.steps }}.area()
    }

    fun part2(input: List<String>): Long {
        return input.map { line -> line.toInstruction()
            .let { DigDirection.entries[(it.color.last().digitToInt())] to it.color.dropLast(1).toInt(16) }}.area()
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day18_test")
    check(part1(testInput) == 62L)
    check(part2(testInput) == 952408144115L)

    val input = readInput("Day18")
    part1(input).println()
    part2(input).println()
}