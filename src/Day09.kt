fun main() {

    fun generateNextSequence(input: List<Long>): List<Long> =
        input.zipWithNext()
            .map { (first, second) ->
                second - first
            }

    fun createSequences(input: List<Long>): Sequence<List<Long>> =
        generateSequence(input) { list ->
            generateNextSequence(list)
                .takeIf { sequence ->
                    sequence.any { it != 0L }
                }
        }

    fun extrapolateNext(input: List<Long>): Long =
        createSequences(input).map { it.last() }
            .reduce { acc, l -> acc + l }

    fun part1(input: List<String>): Long =
        input.sumOf { line ->
            extrapolateNext(
                line.split(" ")
                    .map { it.toLong() }
            )
        }

    fun extrapolatePrevious(input: List<Long>): Long =
        createSequences(input).map { it.first() }
            .toList().reversed()
            .reduce { acc, l -> l - acc  }

    fun part2(input: List<String>): Long =
        input.sumOf { line ->
            extrapolatePrevious(
                line.split(" ")
                    .map { it.toLong() }
            )
        }

    run("Day09".createFiles(), ::part1, ::part2, 114, 2)

}