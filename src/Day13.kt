fun main() {
    data class Image(private val grid: List<String>) {
        fun findHorizontalMirror(diffs: Int): Int? =
            (1 ..< grid.size).find {
                mirrors(it, grid.size).sumOf { (up, down) -> grid[up].diff(grid[down]) } == diffs
            }?.let { it * 100}

        fun findVerticalMirror(diffs: Int): Int? =
            (1 ..< grid.first().length).find {
                mirrors(it, grid.first().length).sumOf { (left, right) -> column(left).diff(column(right)) } == diffs
            }

        fun String.diff(other: String): Int = indices.count { this[it] != other[it] }

        fun mirrors(from: Int, to: Int): List<Pair<Int, Int>> = (0..< from).reversed().zip(from..< to)

        fun column(column: Int): String = grid.map { it[column] }.joinToString("")

        fun reflections(): Int {
            return findHorizontalMirror(0) ?: findVerticalMirror(0) ?: error("No mirror")
        }

        fun brokenReflections(): Int {
            return findHorizontalMirror(1) ?: findVerticalMirror(1) ?: error("No mirror")
        }
    }

    fun List<String>.toImage() = Image(this)

    fun part1(input: List<String>): Int {
        return input.fold(mutableListOf(mutableListOf<String>())) { acc, s ->
            if (s.isBlank()) acc.add(mutableListOf()) else acc.last().add(s)
            acc
        }.sumOf { it.toImage().reflections() }
    }

    fun part2(input: List<String>): Int {
        return input.fold(mutableListOf(mutableListOf<String>())) { acc, s ->
            if (s.isBlank()) acc.add(mutableListOf()) else acc.last().add(s)
            acc
        }.sumOf { it.toImage().brokenReflections() }
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day13_test")
    check(part1(testInput) == 405)
    check(part2(testInput) == 400)

    val input = readInput("Day13")
    part1(input).println()
    part2(input).println()
}
