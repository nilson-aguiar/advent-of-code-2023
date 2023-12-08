import java.util.stream.LongStream
import java.util.stream.Stream

fun main() {
    val finalType = "location"
    fun part1(input: List<String>): Long {
        val (seeds, maps) = mapData(input)

        return seeds
            .map {
                Location(it.toLong(), it.toLong(), "seed")
            }
            .minOf { location ->
                var current = location

                do {
                    val converter = maps[current.type]!!
                    val convertsTo = converter.first().destination.type
                    current = maps[current.type]?.filter {
                        it.source.run {
                            current.start in start..end
                        }
                    }?.map {
                        val diff = current.start - it.source.start
                        val to = it.destination.start + diff

                        Location(to, to, convertsTo)
                    }?.firstOrNull() ?: Location(current.start, current.end, convertsTo)

                } while (current.type != finalType)

                current.start
            }

    }

    fun part2(input: List<String>): Long {

        val (seeds, maps) = mapData(input)

        //TODO: Invert logic from location to seeds then fetch the lowest, the amount of locations is smaller

        return seeds
            .windowed(size= 2, step = 2)
            .stream()
            .map {
                val startsAt = it.first().toLong()
                val endsAt = startsAt + it.last().toLong()

                LongStream.range(startsAt, endsAt)
                    .mapToObj { seed ->
                        Location(seed, seed, "seed")
                    }
            }
            .reduce { first, second -> Stream.concat(first, second) }
            .get()
            .parallel()
            .mapToLong { location ->
                var current = location

                do {
                    val converter = maps[current.type]!!
                    val convertsTo = converter.first().destination.type
                    current = maps[current.type]?.filter {
                        it.source.run {
                            current.start in start..end
                        }
                    }?.map {
                        val diff = current.start - it.source.start
                        val to = it.destination.start + diff

                        Location(to, to, convertsTo)
                    }?.firstOrNull() ?: Location(current.start, current.end, convertsTo)

                } while (current.type != finalType)

                current.start
            }.min().asLong
    }

    run("Day05".createFiles(), ::part1, ::part2, 35L, 46L)
}

private fun mapData(input: List<String>): Pair<List<String>, MutableMap<String, MutableList<Converter>>> {
    val seeds = input.first().removePrefix("seeds: ").split(" ")

    val maps = mutableMapOf<String, MutableList<Converter>>()
    val mapIdentifier = " map:"
    var mapId = ""
    input.subList(2, input.size)
        .forEach { line ->
            if (line.isBlank()) {
                return@forEach
            } else if (line.contains(mapIdentifier)) {
                mapId = line.removeSuffix(mapIdentifier).trim()
            } else {
                val conversionInfo = mapId.split("-to-")
                val from = conversionInfo.first().trim()
                val to = conversionInfo.last().trim()

                val values = line.split(" ").filter { it.isNotBlank() }.map { it.toLong() }
                val range = values.last() - 1

                val destination = values[0].let { Location(it, it + range, to) }
                val source = values[1].let { Location(it, it + range, from) }

                val conversion = Converter(
                    source, destination, mapId
                )

                val list = maps.getOrDefault(from, mutableListOf())
                list.add(conversion)
                maps[from] = list
            }
        }
    return Pair(seeds, maps)
}

private data class Converter(
    val source: Location,
    val destination: Location,
    val convertTo: String
)

private data class Location(
    val start: Long,
    val end: Long,
    val type: String
)