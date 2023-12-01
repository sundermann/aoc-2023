fun main() {
    fun part1(input: List<String>): Int {
        return input.sumOf { "${it.find { it.isDigit() }}${it.findLast { it.isDigit() }}".toInt() }
    }

    fun parse(input: String): Int {
        val digits = listOf("1", "2", "3", "4", "5", "6", "7", "8", "9",
            "one", "two", "three", "four", "five", "six", "seven", "eight", "nine")
            .mapIndexed { index, digit -> Pair(digit, (index % 9 + 1).toString()) }
            .toMap()

        val first = input.findAnyOf(digits.keys)?.second ?: error("Not found")
        val last = input.findLastAnyOf(digits.keys)?.second ?: error("Not found")
        return "${digits[first]}${digits[last]}".toInt()
    }

    fun part2(input: List<String>): Int {
        return input.sumOf { parse(it) }
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day01_test")
    check(part1(testInput) == 142)

    val input = readInput("Day01")
    part1(input).println()
    part2(input).println()
}
