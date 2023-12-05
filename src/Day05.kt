fun main() {

    data class Mapping(val dst: Long, val src: Long, val length: Long)
    data class AlmanacMap(val from: String, val to: String, val mappings: List<Mapping>) {
        fun map(seed: Long): Long {
            return mappings.find { seed in (it.src ..< it.src + it.length) }?.let { it.dst + seed - it.src } ?: seed
        }
    }

    fun List<String>.toSeedMap(): List<AlmanacMap> {
        val maps = fold(mutableListOf(mutableListOf<String>())) { acc, s ->
            if (s.isBlank())
                acc.add(mutableListOf())
            else
                acc.last().add(s)
            acc
        }.map { list ->
            val (from, _, to) = list.first().split('-', ' ')
            val blocks = list.drop(1).map {
                val (dst, src, len) = it.split(' ').map(String::toLong)
                Mapping(dst, src, len)
            }
            AlmanacMap(from, to, blocks)
        }

        return maps
    }

    fun part1(input: List<String>): Long {
        val maps = input.drop(2).toSeedMap()
        return input.first().split(" ").drop(1).map { it.toLong() }
            .minOf { seed -> maps.fold(seed) { acc, place -> place.map(acc) } }
    }

    fun part2(input: List<String>): Long {
        val maps = input.drop(2).toSeedMap()
        return input.first().split(" ")
            .drop(1).map { it.toLong() }
            .chunked(2)
            .minOf { (start, length) ->  (start ..< start + length)
                .minOf { seed -> maps.fold(seed) { acc, place -> place.map(acc) } }
            }
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day05_test")
    check(part1(testInput) == 35L)
    check(part2(testInput) == 46L)

    val input = readInput("Day05")
    part1(input).println()
    part2(input).println()
}
