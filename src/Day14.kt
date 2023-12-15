private enum class Dir { NORTH, EAST, SOUTH, WEST }
fun main() {
    data class Image(private val grid: List<String>) {
        fun String.slide(): String {
            val slided = toMutableList()
            slided.forEachIndexed { i, c ->
                if (c == 'O') {
                    val swap = (0..< i).reversed().find { slided[it] != '.' }?.let { it + 1 }?: 0
                    slided[i] = '.'
                    slided[swap] = 'O'
                }
            }
            return slided.joinToString("")
        }
        fun List<String>.column(column: Int): String = this.map { it[column] }.joinToString("")

        fun slide(dir: Dir): Image {
            return when(dir) {
                Dir.NORTH -> {
                    val columns = grid[0].indices.map { grid.column(it).slide() }
                    Image(columns.first().indices.map { columns.column(it) })
                }
                Dir.EAST ->Image(grid.map { it.reversed().slide().reversed() })
                Dir.SOUTH -> {
                    val columns = grid[0].indices.map { grid.column(it).reversed().slide().reversed() }
                    Image(columns.first().indices.map { columns.column(it) })
                }
                Dir.WEST -> Image(grid.map { it.slide() })
            }
        }

        fun load() = grid.reversed().mapIndexed { i, line -> (i + 1) * line.count { it == 'O' } }.sum()
    }

    fun List<String>.toImage() = Image(this)

    fun part1(input: List<String>): Int {
        return input.toImage().slide(Dir.NORTH).load()
    }

    fun slideSequence() = sequence {
        repeat(1000000000) {
            yieldAll(listOf(Dir.NORTH, Dir.WEST, Dir.SOUTH, Dir.EAST))
        }
    }

    fun part2(input: List<String>): Int {
        var current = input.toImage()
        val lastCycles = mutableListOf<Image>()
        var i = 0
        for (dir in slideSequence()) {
            current = current.slide(dir)

            if(dir == Dir.EAST) {
                if (current in lastCycles)
                    break
                i++

                lastCycles += current
            }
        }

        val cycleStart = lastCycles.indexOf(current)
        val cycle = lastCycles.subList(cycleStart, lastCycles.size)

        return cycle[(1000000000 - i - 1) % cycle.size].load()
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day14_test")
    check(part1(testInput) == 136)
    check(part2(testInput) == 64)

    val input = readInput("Day14")
    part1(input).println()
    part2(input).println()
}
