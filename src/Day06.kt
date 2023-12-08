fun main() {
    fun part1(input: List<String>): Int {
        return input.asSequence().map {
            it.split(":").last().trim()
        }
            .zipWithNext()
            .map {
                val raceTimes = it.first.split("  ").filter { it.isNotBlank() }
                val raceRecords = it.second.split("  ").filter { it.isNotBlank() }


                raceTimes.zip(raceRecords)
                    .map { (first, second) ->
                        Race(first.trim().toLong(), second.trim().toLong())
                    }

            }
            .flatten()
            .map { (raceTime, bestTime) ->
                LongRange(1, raceTime - 1).map {
                    (raceTime - it) * it
                }.count {
                    it > bestTime
                }
            }.product()
    }

    fun part2(input: List<String>): Number {
        return input.map {
            it.split(":").last().trim()
        }
            .zipWithNext()
            .map {
                val raceTimes = it.first.replace(" ", "").trim().toLong()
                val raceRecords = it.second.replace(" ", "").trim().toLong()

                Race(raceTimes, raceRecords)
            }.first().let { (raceTime, bestTime) ->

                val first = LongRange(1, raceTime - 1).asSequence()
                    .map {
                        it to (raceTime - it) * it
                    }.first {
                        it.second > bestTime
                    }.first


                val last = LongRange(1, raceTime - 1).reversed().asSequence()
                    .map {
                        it to (raceTime - it) * it
                    }.first {
                        it.second > bestTime
                    }.first

                last - first + 1
            }
    }

    run("Day06".createFiles(), ::part1, ::part2, 288, 71503L)
}

private data class Race(
    val time: Long,
    val record: Long
)