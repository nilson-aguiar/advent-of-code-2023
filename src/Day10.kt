fun main() {

    fun List<String>.readMap() =
        PipeMap(
            this.mapIndexed { x, line ->
                x to line.mapIndexed { y, position ->
                    y to position
                }.associateBy { it.first }
                    .mapValues { it.value.second }
            }
                .associateBy { it.first }
                .mapValues { it.value.second }
        )


    fun part1(input: List<String>): Int {

        val map = input.readMap()

        val (_, startTo) = map.start

        var stepCount = 1
        generateSequence(startTo) { steps ->
            steps.mapNotNull { step ->
                map.next(step)
            }
                .also { stepCount++ }
                .takeIf { it.isNotEmpty() && it.size == it.map { step -> step.location }.toSet().size }
        }.toList()
//            .forEachIndexed { index, pipeSteps -> println("#$index -> $pipeSteps") }


        return stepCount
    }

    fun part2(input: List<String>): Int {

        val map = input.readMap()

        val (_, startTo) = map.start

        var stepCount = 1
        generateSequence(startTo) { steps ->
            steps.mapNotNull { step ->
                map.next(step)
            }
                .also { stepCount++ }
                .takeIf { it.isNotEmpty() }
        }.toList()

        stepCount.println()



        return input.sumOf { it.length }
    }

    run("Day10".createFiles(), ::part1, ::part2, 4, 200)
}

private data class PipeMap(
    val map: Map<Int, Map<Int, Char>>
) {
    val start = map.firstNotNullOf { (x, row) ->
        row.firstNotNullOfOrNull { (y, char) ->
            if (char == 'S')
                PipeLocation(x, y)
            else null
        }
    }.let { l ->
        l to
        listOfNotNull(
            this[l.north()]
                .takeIf { it in PipeConnection.withSouth().map { on -> on.first.char } }
                ?.let { PipeConnection.fromChar(it)?.run { PipeStep(l.north(), this, PipeDirection.SOUTH) } },
            this[l.south()]
                .takeIf { it in PipeConnection.withNorth().map { on -> on.first.char } }
                ?.let { PipeConnection.fromChar(it)?.run { PipeStep(l.south(), this, PipeDirection.NORTH) } },
            this[l.east()]
                .takeIf { it in PipeConnection.withWest().map { on -> on.first.char } }
                ?.let { PipeConnection.fromChar(it)?.run { PipeStep(l.east(), this, PipeDirection.WEST) } },
            this[l.west()]
                .takeIf { it in PipeConnection.withEast().map { on -> on.first.char } }
                ?.let { PipeConnection.fromChar(it)?.run { PipeStep(l.west(), this, PipeDirection.EAST) } }
        )
    }

    fun next(step: PipeStep): PipeStep? {
        val next  = step.next()

        return this[next]?.toPipeConnection()?.let {
            PipeStep(
                location = next,
                connection = it,
                from = step.to.inverted()
            )
        }
    }

    operator fun get(location: PipeLocation): Char? =
        map.get(location.x)?.get(location.y)

}

private data class PipeLocation(val x: Int, val y: Int) {

    fun north() = PipeLocation(x - 1, y)
    fun south() = PipeLocation(x + 1, y)
    fun east() = PipeLocation(x, y + 1)
    fun west() = PipeLocation(x, y - 1)

}

private data class PipeStep(
    val location: PipeLocation,
    val connection: PipeConnection,
    val from: PipeDirection
) {
    val to: PipeDirection = connection.connections.run {
        if(from == first) second else first
    }

    fun next() = to.step(location)
}

private enum class PipeDirection {
    NORTH, SOUTH, EAST, WEST;

    fun step(location: PipeLocation) =
        when(this) {
            NORTH -> location.north()
            SOUTH -> location.south()
            EAST -> location.east()
            WEST -> location.west()
        }

    fun inverted(): PipeDirection =
        when(this) {
            NORTH -> SOUTH
            SOUTH -> NORTH
            EAST -> WEST
            WEST -> EAST
        }

}

private fun Char.toPipeConnection() = PipeConnection.fromChar(this)

private enum class PipeConnection(
    val char: Char,
    val connections: Pair<PipeDirection, PipeDirection>
) {

    PIPE('|', PipeDirection.NORTH to PipeDirection.SOUTH),
    DASH('-', PipeDirection.EAST to PipeDirection.WEST),
    L('L', PipeDirection.NORTH to PipeDirection.EAST),
    J('J', PipeDirection.NORTH to PipeDirection.WEST),
    SEVEN('7', PipeDirection.SOUTH to PipeDirection.WEST),
    F('F', PipeDirection.SOUTH to PipeDirection.EAST);

    companion object {
        private fun filterAndMap(pipeDirection: PipeDirection) =
            entries.filter {
                val (first, second) = it.connections
                first == pipeDirection || second == pipeDirection
            }.map {
                val (first, second) = it.connections
                it to (if (first == pipeDirection) first else second)
            }

        fun withNorth() = filterAndMap(PipeDirection.NORTH)
        fun withSouth() = filterAndMap(PipeDirection.SOUTH)
        fun withEast() = filterAndMap(PipeDirection.EAST)
        fun withWest() = filterAndMap(PipeDirection.WEST)


        fun fromChar(char: Char) = entries.firstOrNull { it.char == char }
    }

}
