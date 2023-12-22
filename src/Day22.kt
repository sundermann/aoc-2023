fun main() {
    data class Point3D(val x: Int, val y: Int, val z: Int)
    data class Brick(val from: Point3D, val to: Point3D)

    fun String.toBrick(): Brick {
        val (first, second) = split('~').map { it.split(',').map { it.toInt() } }
        return Brick(Point3D(first[0], first[1], first[2]), Point3D(second[0], second[1], second[2]))
    }

    fun List<Set<Point3D>>.fallDown(): Pair<List<Set<Point3D>>, Int> {
        val output = mutableListOf<Set<Point3D>>()
        val fallen = hashSetOf<Point3D>()
        var id = 0

        sortedBy { it.minOf { p -> p.z } }.forEach { brick ->
            var current = brick
            while (true) {
                val down = current.mapTo(hashSetOf()) { p -> p.copy(z = p.z - 1) }
                if (down.any { it in fallen || it.z <= 0 }) {
                    fallen += current
                    output += current
                    if (current != brick)
                        id++
                    break
                }

                current = down
            }
        }

        return output to id
    }

    fun List<Brick>.toPointMap() = map { (a, b) ->
        (a.x..b.x).map { x ->
            (a.y..b.y).map { y ->
                (a.z..b.z).map { z -> Point3D(x, y, z) }
            }
        }.flatten().flatten().toHashSet()
    }


    fun part1(input: List<String>): Int {
        val bricks = input.map { it.toBrick() }.toPointMap()

        val start = bricks.fallDown().first
        val simulations = start.map { start.minusElement(it).fallDown().second }

        return simulations.count { it == 0 }
    }

    fun part2(input: List<String>): Int {
        val bricks = input.map { it.toBrick() }.toPointMap()

        val start = bricks.fallDown().first
        val simulations = start.map { start.minusElement(it).fallDown().second }

        return simulations.sum()
    }

    val testInput = readInput("Day22_test")
    check(part1(testInput) == 5)
    check(part2(testInput) == 7)

    val input = readInput("Day22")
    part1(input).println()
    part2(input).println()
}