fun main() {
    data class Race(val time: Long, val distance: Long) {
        fun numWins(): Int {
            return (0 ..< time).count { it * (time - it) > distance }
        }
    }

    fun List<String>.toRaces(): List<Race> {
        val distances = first().split(':', ' ').drop(1).filter { it.isNotBlank() }.map { it.toLong() }
        val times = last().split(':', ' ').drop(1).filter { it.isNotBlank() }.map { it.toLong() }

        return distances.zip(times).map { (distance, time) -> Race(distance, time) }
    }

    fun List<String>.toSingleRace(): Race {
        val distance = first().filter { it.isDigit() }.toLong()
        val time = last().filter { it.isDigit() }.toLong()

        return Race(distance, time)
    }

    fun part1(input: List<String>): Int {
        return input.toRaces().map { it.numWins() }.reduce(Int::times)
    }

    fun part2(input: List<String>): Int {
        return input.toSingleRace().numWins()
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day06_test")
    check(part1(testInput) == 288)
    check(part2(testInput) == 71503)

    val input = readInput("Day06")
    part1(input).println()
    part2(input).println()
}
