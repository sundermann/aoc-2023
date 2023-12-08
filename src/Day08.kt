fun main() {

    data class Node(val root: String, val left: String, val right: String) {
        fun next(instruction: Char): String = if (instruction == 'L') left else right
    }

    fun String.toNode(): Node {
        val (root, left, right) = split('=', ',')
        return Node(root.trim(), left.trim().drop(1), right.trim().dropLast(1))
    }

    fun gcd(a: Long, b: Long): Long = if (b == 0L) a else gcd(b, a % b)
    fun lcm(a: Long, b: Long) = a * b / gcd(a, b)

    fun part1(input: List<String>): Int {
        val instruction = input.first()
        val nodeMap = input.drop(2).map { it.toNode() }.associateBy { it.root }

        var steps = 0
        var current = "AAA"
        while (current != "ZZZ") {
            current = nodeMap[current]!!.next(instruction[(steps++ % instruction.length)])
        }

        return steps
    }

    fun part2(input: List<String>): Long {
        val instruction = input.first()
        val nodeMap = input.drop(2).map { it.toNode() }.associateBy { it.root }

        val starts = nodeMap.keys.filter { it.endsWith('A') }
        val steps = LongArray(starts.size) { 0L }
        starts.forEachIndexed { i, start ->
            var current = start
            while (!current.endsWith('Z')) {
                current = nodeMap[current]!!.next(instruction[(steps[i]++ % instruction.length).toInt()])
            }
        }

        return steps.reduce { acc, step -> lcm(acc, step) }
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day08_test")
    //check(part1(testInput) == 6)
    check(part2(testInput) == 6L)

    val input = readInput("Day08")
    part1(input).println()
    part2(input).println()
}
