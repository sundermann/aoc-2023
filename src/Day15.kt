fun main() {
    fun String.toHASH() = split(',')
    fun String.HASH() = fold(0) { acc, c -> ((acc + c.code) * 17) % 256 }

    fun part1(input: List<String>): Int {
        return input.first().toHASH().sumOf { it.HASH() }
    }

    fun part2(input: List<String>): Int {
        val boxes = List(256) { MutableList(0) { "" to 0 } }
        input.first().toHASH().forEach {
            val label = it.takeWhile { it != '=' && it != '-' }
            val operation = it.find { it == '=' || it == '-' }!!
            val hash = label.HASH()
            val lenses = boxes[hash]

            if (operation == '-') {
                val lens = lenses.indexOfFirst { (l, f) -> l == label }
                if (lens >= 0)
                    lenses.removeAt(lens)
            }

            if (operation == '=') {
                val focalLength = it.last().digitToInt()
                val lens = lenses.indexOfFirst { (l, f) -> l == label }
                if (lens == -1)
                    lenses.add(label to focalLength)
                else
                    lenses[lens] = label to focalLength
            }
        }
        return boxes.mapIndexed { i, b -> (i + 1) * b.mapIndexed{ j, (l, f) -> (j + 1) * f }.sum() }.sum()
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day15_test")
    check(part1(testInput) == 1320)
    check(part2(testInput) == 145)

    val input = readInput("Day15")
    part1(input).println()
    part2(input).println()
}
