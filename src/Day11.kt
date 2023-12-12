fun main() {
    data class Position(val x: Int, val y: Int) {
        fun walk(other: Position) = (minOf(x, other.x)..<maxOf(x, other.x)).map { x -> Position(x + 1, y) } +
                (minOf(y, other.y)..<maxOf(y, other.y)).map { y -> Position(x, y + 1) }
    }
    class Image(val grid: List<List<Int>>, val factor: Int = 2) {

        fun<U> List<List<U>>.transpose() = List(this[0].size) { j -> List(this.size) { i -> this[i][j] } }

        fun expand() = grid.map { if (it.all { it > 0 }) List(it.size) { factor } else it }.transpose()
                .map { if (it.all { it > 0 }) List(it.size) { factor } else it }.transpose()
                .let { Image(it) }

        operator fun get(position: Position) = grid[position.x][position.y]

        fun findGalaxies() = grid.indices.map { it to grid[0].indices }
                .flatMap { x -> x.second.map { y -> Position(x.first, y) } }
                .filter { this[it] == 0 }
    }

    fun List<String>.toGalaxy(factor: Int) = Image(this.map {
        it.replace('.', '1').replace('#', '0')
                .map { it.digitToInt() } }, factor)

    fun <T, U> cartesianProduct(c1: Collection<T>, c2: Collection<U>): List<Pair<T, U>> {
        return c1.flatMap { i -> c2.map { j -> i to j } }
    }

    fun part1(input: List<String>, factor: Int = 2): Long {
        val galaxy = input.toGalaxy(factor).expand()
        val galaxies = galaxy.findGalaxies()

        return cartesianProduct(galaxies, galaxies)
                .sumOf { (g1, g2) -> g1.walk(g2).map { galaxy[it].toLong() }.sumOf { maxOf(1L, it) } } / 2L
    }

    fun part2(input: List<String>): Long {
        return part1(input, 1000000)
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day11_test")
    check(part1(testInput) == 374L)
    //check(part2(testInput) == 4)

    val input = readInput("Day11")
    part1(input).println()
    part2(input).println()
}
