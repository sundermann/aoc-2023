fun main() {
    data class Schematic(val grid: List<List<Char>>) {
        fun hasAdjacentSymbol(x: Int, y: Int) = neighbourSymbols(x, y).any { it != '.' && !it.isDigit() }

        fun neighbours(x: Int, y: Int): List<Pair<Int, Int>> {
            return listOf(Pair(x - 1, y), Pair(x + 1, y), Pair(x, y - 1), Pair(x, y + 1),
                Pair(x - 1, y - 1), Pair(x - 1, y + 1), Pair(x + 1, y - 1), Pair(x + 1, y + 1))
                .filter { (x, y) -> x >= 0 && y >= 0 && x < grid.size && y < grid[x].size }
        }

        fun neighbourSymbols(x: Int, y: Int) = neighbours(x, y).map { (x, y) -> grid[x][y] }

        fun number(x: Int, y: Int) = (grid[x].subList(0, y).takeLastWhile { it.isDigit() } +
                grid[x].subList(y, grid[x].size).takeWhile { it.isDigit() })
                .joinToString("").toInt()
    }
    fun List<String>.toSchematic() = Schematic(this.map { it.toCharArray().toList() })

    fun part1(input: List<String>): Int {
        val schematic = input.toSchematic()

        var sum = 0
        for (x in 0 until schematic.grid.size) {
            var y = 0
            while (y < schematic.grid[x].size) {
                if (schematic.grid[x][y].isDigit()) {
                    val remaining = schematic.grid[x].subList(y, schematic.grid[x].size)
                    val num = remaining.takeWhile { it.isDigit() }
                    if ((y until y + num.size).any { schematic.hasAdjacentSymbol(x, it) })
                        sum += num.joinToString("").toInt()
                    y += num.size
                } else
                    y++
            }
        }

        return sum
    }

    fun part2(input: List<String>): Int {
        val schematic = input.toSchematic()

        var sum = 0
        for (x in 0 until schematic.grid.size) {
            for (y in 0 until schematic.grid[x].size) {
                if (schematic.grid[x][y] == '*') {
                    val digitNeighbours = schematic.neighbours(x, y).filter { (x, y) -> schematic.grid[x][y].isDigit() }
                    val gears = digitNeighbours.map { (x, y) -> schematic.number(x, y) }.distinct()
                    if (gears.size == 2)
                        sum += gears[0] * gears[1]
                }
            }
        }

        return sum
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day03_test")
    check(part1(testInput) == 4361)
    check(part2(testInput) == 467835)

    val input = readInput("Day03")
    part1(input).println()
    part2(input).println()
}
