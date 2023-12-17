import java.util.*

data class Point(val x: Int, val y: Int) {
    operator fun plus(other: Point) = Point(x + other.x, y + other.y)
}

enum class Orientation {
    UP, RIGHT, DOWN, LEFT;

    val left get() = entries[(ordinal - 1 + entries.size) % entries.size]
    val right get() = entries[(ordinal + 1) % entries.size]

    val vector: Point
        get() = when(this) {
            UP -> Point(-1, 0)
            RIGHT -> Point(0, 1)
            DOWN -> Point(1, 0)
            LEFT -> Point(0, -1)
        }
}

fun main() {
    data class Pose(val point: Point, val direction: Orientation, val directionCount: Int) {
        fun neighbours(ultra: Boolean = false): List<Pose> {
            return buildList {
                if (directionCount < if (ultra) 10 else 3) {
                    add(Pose(point + direction.vector, direction, directionCount + 1))
                }
                if (!ultra || (directionCount >= 4 || directionCount == 0)) {
                    add(Pose(point + direction.left.vector, direction.left, 1))
                    add(Pose(point + direction.right.vector, direction.right, 1))
                }
            }
        }
    }

    data class SeenPose(val cost: Int, val prev: Pose?)

    data class ScoredPose(val vertex: Pose, val score: Int) : Comparable<ScoredPose> {
        override fun compareTo(other: ScoredPose) = score.compareTo(other.score)
    }

    fun dijkstra(start: Pose, endFunction: (Pose) -> Boolean, neighbours: (Pose) -> Iterable<Pose>, cost: (Pose) -> Int): Int {
        val openList = PriorityQueue(listOf(ScoredPose(start, 0)))
        var end: Pose? = null
        val visited = mutableMapOf(start to SeenPose(0, null))

        while (end == null) {
            val (currentVertex, currentScore) = openList.remove()
            end = if (endFunction(currentVertex)) currentVertex else null

            val next = neighbours(currentVertex)
                .filterNot { it in visited }
                .map { next -> ScoredPose(next, currentScore + cost(next)) }

            openList += next
            visited += next.associate { it.vertex to SeenPose(it.score, currentVertex) }
        }

        return visited[end]!!.cost
    }

    fun part1(input: List<String>): Int {
        val map = input.map { line -> line.map { it.digitToInt() } }
        val start = Pose(Point(0, 0), Orientation.RIGHT, 0)
        val end = Point(map[0].lastIndex, map.lastIndex)

        return dijkstra(start, { (p, _) -> p == end },
            { it.neighbours().filter { (p) -> p.y in map.indices && p.x in map.first().indices } },
            { (p) -> map[p.y][p.x] })
    }

    fun part2(input: List<String>): Int {
        val map = input.map { line -> line.map { it.digitToInt() } }
        val start = Pose(Point(0, 0), Orientation.RIGHT, 0)
        val end = Point(map[0].lastIndex, map.lastIndex)
        return dijkstra(start, { (p, _, line) -> p == end && line >= 4 },
            { it.neighbours(true).filter { (p) -> p.y in map.indices && p.x in map.first().indices } },
            { (p) -> map[p.y][p.x] })
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day17_test")
    check(part1(testInput) == 102)
    check(part2(testInput) == 94)

    val input = readInput("Day17")
    part1(input).println()
    part2(input).println()
}
