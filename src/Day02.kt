fun main() {
    data class GameSet(val blue: Int, val red: Int, val green: Int) {
        fun power() = blue * red * green
    }
    data class Game(val id: Int, val sets: List<GameSet>) {
        fun fewestCubes(): GameSet {
            return GameSet(sets.maxOf { it.blue }, sets.maxOf { it.red }, sets.maxOf { it.green })
        }
    }

    fun String.toGameSet(): GameSet {
        val contents = this.split(", ").map { it.trim() }
        val green = contents.find { it.contains("green") }?.split(" ")?.first()?.toInt() ?: 0
        val blue = contents.find { it.contains("blue") }?.split(" ")?.first()?.toInt() ?: 0
        val red = contents.find { it.contains("red") }?.split(" ")?.first()?.toInt() ?: 0
        return GameSet(blue, red, green)
    }

    fun String.toGame(): Game {
        return Game(this.split(":").first().split(" ").last().toInt(),
            this.split(": ").last().split(";").map { it.toGameSet() })
    }

    fun part1(input: List<String>): Int {
        return input
            .map { it.toGame() }
            .filter { it.sets.all { it.red <= 12 && it.green <= 13 && it.blue <= 14 } }
            .sumOf { it.id }
    }

    fun part2(input: List<String>): Int {
        return input.map { it.toGame() }
            .map { it.fewestCubes() }
            .sumOf { it.power() }
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day02_test")
    check(part1(testInput) == 8)
    check(part2(testInput) == 2286)

    val input = readInput("Day02")
    part1(input).println()
    part2(input).println()
}
