fun main() {
    data class GameCard(val winningNumbers: Set<Int>, val numbers: List<Int>) {
        fun points() = wins().let { if (it == 0) 0 else 1 shl (it - 1) }

        fun wins() = numbers.count { it in winningNumbers }
    }
    fun String.toGameCard(): GameCard {
        val (_, win, numbers) = split(":", " | ")
        return GameCard(win.chunked(3).map { it.trim().toInt() }.toSet(),
            numbers.chunked(3).map { it.trim().toInt() })
    }

    fun part1(input: List<String>): Int {
        return input.sumOf { it.toGameCard().points() }
    }

    fun part2(input: List<String>): Int {
        val scratchCards = MutableList(input.size) {1}

        input.map { it.toGameCard() }.forEachIndexed { i, gameCard ->
            (1 .. gameCard.wins()).map { scratchCards[i + it] += scratchCards[i] }}

        return scratchCards.sum()
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day04_test")
    check(part1(testInput) == 13)
    check(part2(testInput) == 30)

    val input = readInput("Day04")
    part1(input).println()
    part2(input).println()
}
