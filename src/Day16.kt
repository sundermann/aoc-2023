fun main() {
    data class Grid(private val grid: List<String>) {
        private val rowDirections = listOf(-1,  0, 1,  0)
        private val columnDirections = listOf(0, 1,  0, -1)
        private val transitions = listOf(
            listOf(".|", "/-", "", "\\-"),
            listOf("/|", ".-", "\\|", ""),
            listOf("", "\\-", ".|", "/-"),
            listOf("\\|", "", "/|", ".-"),
        )

        val beams = List(grid.size) { List(grid.first().length) { MutableList(4) { false } } }

        fun go(index: Pair<Int, Int>, direction: Int): Grid {
            val (row, col) = index
            if (row < 0 || grid.size <= row || col < 0 || grid.first().length <= col || beams[row][col][direction])
                return this
            beams[row][col][direction] = true

            (0..<4).map { next ->
                if (grid[row][col] in transitions[direction][next])
                    go(row + rowDirections[next] to col + columnDirections[next], next)
            }
            return this
        }

        fun countBeams(): Int {
            return beams.sumOf { it.sumOf { (if (it.any { it }) 1 else 0).toInt() } }
        }
    }

    fun List<String>.toGrid() = Grid(this)

    fun part1(input: List<String>): Int {
        return input.toGrid().go(0 to 0, 1).countBeams()
    }

    fun part2(input: List<String>): Int {
        return sequence {
            yieldAll(input.indices.map { it to 0 to 1 })
            yieldAll(input.indices.map { it to input.first().lastIndex to 0 })
            yieldAll((input.first().indices).map { 0 to it to 2 })
            yieldAll((input.first().indices).map { input.lastIndex to it to 3 })
        }.maxOf { (pos, dir) -> input.toGrid().go(pos, dir).countBeams() }
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day16_test")
    check(part1(testInput) == 46)
    check(part2(testInput) == 51)

    val input = readInput("Day16")
    part1(input).println()
    part2(input).println()
}
