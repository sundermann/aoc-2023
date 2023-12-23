fun main() {

    fun findLongestPath(current: Point, end: Point, visited: Array<BooleanArray>, distance: Int,
                        neighbours: (Point) -> List<Pair<Point, Int>>): Int {
        if (current == end) {
            return distance
        }

        visited[current.x][current.y] = true
        return neighbours(current)
            .filter { (neighbour, _) -> !visited[neighbour.x][neighbour.y] }
            .maxOfOrNull { (neighbour, weight) ->
                findLongestPath(neighbour, end, visited, distance + weight, neighbours)
            }.also {
                visited[current.x][current.y] = false
            }?: 0
    }

    fun Point.neighbours() = listOf(Point(x - 1, y), Point(x + 1, y), Point(x, y - 1), Point(x, y + 1))
    fun List<List<Char>>.inBounds(p: Point) = p.x in indices && p.y in first().indices

    fun part1(input: List<String>): Int {
        val start = Point(0, 1)
        val grid = input.map { it.toList() }
        val end = Point(grid.lastIndex, grid.first().lastIndex - 1)
        val visited = Array(grid.size) { BooleanArray(grid.first().size) }

        return findLongestPath(start, end, visited, 0) { current ->
            when (grid[current.x][current.y]) {
                '.' -> current.neighbours().filter { grid.inBounds(it) && grid[it.x][it.y] in ".<>^v" }.map { it to 1 }
                '>' -> listOf(current.copy(y = current.y + 1) to 1)
                '<' -> listOf(current.copy(y = current.y - 1) to 1)
                'v' -> listOf(current.copy(x = current.x + 1) to 1)
                '^' -> listOf(current.copy(x = current.x - 1) to 1)
                else -> error("Wrong input")
            }
        }
    }

    fun part2(input: List<String>): Int {
        val start = Point(0, 1)
        val grid = input.map { it.toList() }
        val end = Point(grid.lastIndex, grid.first().lastIndex - 1)

        val junctions = mutableMapOf(start to mutableListOf<Pair<Point, Int>>(), end to mutableListOf())
        grid.indices.forEach { row ->
            grid[row].indices
                .filter { grid[row][it] == '.' }
                .map { Point(row, it) }
                .filter { it.neighbours().filter { grid.inBounds(it) && grid[it.x][it.y] in ".<>^v" }.size > 2 }
                .forEach { junctions[it] = mutableListOf() }
        }

        junctions.keys.forEach { junction ->
            val visited = mutableSetOf(junction)
            var distance = 0

            var current = setOf(junction)
            while (current.isNotEmpty()) {
                distance++
                current = buildSet {
                    current.forEach { c ->
                        c.neighbours()
                            .filter { grid.inBounds(it) && grid[it.x][it.y] in ".<>^v" }
                            .filter { it !in visited }.forEach { n ->
                                if (n in junctions)
                                    junctions[junction]!!.add(n to distance)
                                else {
                                    add(n)
                                    visited += n
                                }
                            }
                    }
                }
            }
        }

        val visited = Array(grid.size) { BooleanArray(grid.first().size) }
        return findLongestPath(start, end, visited, 0) { current -> junctions[current]!! }
    }

    val testInput = readInput("Day23_test")
    check(part1(testInput) == 94)
    check(part2(testInput) == 154)

    val input = readInput("Day23")
    part1(input).println()
    part2(input).println()
}