private enum class Direction {
    NORTH, EAST, SOUTH, WEST;

    fun toRight() = entries[(ordinal + 1) % entries.size]
    fun toBack() = entries[(ordinal + 2) % entries.size]
    fun toLeft() = entries[(ordinal + 3) % entries.size]
}

private enum class PipeTile(val char: Char, val connections: Set<Direction>) {
    NORTH_AND_SOUTH('|', setOf(Direction.NORTH, Direction.SOUTH)),
    EAST_AND_WEST('-', setOf(Direction.EAST, Direction.WEST)),
    SOUTH_AND_EAST('L', setOf(Direction.NORTH, Direction.EAST)),
    SOUTH_AND_WEST('J', setOf(Direction.NORTH, Direction.WEST)),
    NORTH_AND_WEST('7', setOf(Direction.SOUTH, Direction.WEST)),
    NORTH_AND_EAST('F', setOf(Direction.SOUTH, Direction.EAST)),
    EMPTY('.', emptySet()),
    START('S', emptySet());

    fun nextDirection(lastDirection: Direction) = connections.first { it != lastDirection.toBack() }
}

fun main() {
    data class Position(val x: Int, val y: Int) {
        fun move(direction: Direction) = when(direction) {
            Direction.NORTH -> north
            Direction.SOUTH -> south
            Direction.EAST -> east
            Direction.WEST -> west
        }

        val north get() = Position(x, y - 1)
        val east get() = Position(x + 1, y)
        val south get() = Position(x, y + 1)
        val west get() = Position(x - 1, y)
    }

    class Grid(val grid: List<List<PipeTile>>) {
        val start = grid.indexOfFirst { it.contains(PipeTile.START) }
            .let { y -> Position(grid[y].indexOfFirst { it == PipeTile.START }, y) }

        val startingDirection = when {
            get(start.east).connections.contains(Direction.WEST) -> Direction.EAST
            get(start.south).connections.contains(Direction.EAST) -> Direction.WEST
            get(start.west).connections.contains(Direction.NORTH) -> Direction.SOUTH
            get(start.north).connections.contains(Direction.SOUTH) -> Direction.WEST
            else -> error("Should not happen")
        }

        fun inBounds(position: Position) = position.y in grid.indices && position.x in grid[0].indices

        operator fun get(position: Position) = grid[position.y][position.x]

        fun loopPipe() = sequence {
            var position = start
            var direction = startingDirection
            var tile = get(position)

            do {
                position = position.move(direction)
                tile = get(position)
                yield(Triple(position, tile, direction))
                if (tile != PipeTile.START)
                    direction = tile.nextDirection(direction)
            } while(tile != PipeTile.START)
        }
    }

    fun Char.toTile() = PipeTile.entries.first { it.char == this }
    fun List<String>.toWorld() = Grid(map { it.map { it.toTile() } })

    fun part1(input: List<String>): Int {
        return input.toWorld().loopPipe().count() / 2
    }

    fun part2(input: List<String>): Int {
        val world = input.toWorld()

        val loops = world.loopPipe().map { (position, tile, direction) -> position }.toSet()
        val rights = mutableSetOf<Position>()
        val lefts = mutableSetOf<Position>()

        var currentPosition = world.start
        var lastDirection = world.startingDirection
        var clockwise = true

        currentPosition = currentPosition.move(world.startingDirection)
        while (world[currentPosition] != PipeTile.START) {
            val currentTile = world[currentPosition]
            val nextDirection = currentTile.nextDirection(lastDirection)

            var right = lastDirection.toRight()
            var left = lastDirection.toLeft()

            var scanline = currentPosition.move(left)
            while (scanline !in loops && world.inBounds(scanline)) {
                lefts += scanline
                scanline = scanline.move(left)
                if (!world.inBounds(scanline)) clockwise = true
            }

            left = nextDirection.toLeft()
            scanline = currentPosition.move(left)
            while (scanline !in loops && world.inBounds(scanline)) {
                lefts += scanline
                scanline = scanline.move(left)
                if (!world.inBounds(scanline)) clockwise = true
            }

            scanline = currentPosition.move(right)
            while (scanline !in loops && world.inBounds(scanline)) {
                rights += scanline
                scanline = scanline.move(right)
                if (!world.inBounds(scanline)) clockwise = false
            }

            right = nextDirection.toRight()
            scanline = currentPosition.move(right)
            while (scanline !in loops && world.inBounds(scanline)) {
                rights += scanline
                scanline = scanline.move(right)
                if (!world.inBounds(scanline)) clockwise = false
            }

            lastDirection = nextDirection
            currentPosition = currentPosition.move(lastDirection)
        }

        return if (clockwise) rights.size else lefts.size
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day10_test")
    //check(part1(testInput) == 4)
    check(part2(testInput) == 4)

    val input = readInput("Day10")
    part1(input).println()
    part2(input).println()
}
