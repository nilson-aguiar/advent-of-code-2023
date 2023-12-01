import org.assertj.core.api.Assertions.assertThat
import kotlin.time.measureTime

enum class Digit {
    zero, one, two, three, four, five, six, seven, eight, nine
}

fun main() {
    fun part1(input: List<String>): Int {
        return input.sumOf {
            val first = it.firstNotNullOf { char -> char.digitToIntOrNull() }
            val last = it.reversed().firstNotNullOf { char -> char.digitToIntOrNull() }

            "$first$last".toInt()
        }
    }

    fun Int.foundOr(orValue: Int) =
        if (this == -1) orValue
        else this

    fun Digit.findFirstIndex(string: String): Int =
        minOf(
            string.indexOf(name, ignoreCase = true).foundOr(Int.MAX_VALUE),
            string.indexOf("$ordinal").foundOr(Int.MAX_VALUE)
        )

    fun Digit.findLastIndex(string: String): Int =
        maxOf(
            string.lastIndexOf(name, ignoreCase = true).foundOr(Int.MIN_VALUE),
            string.lastIndexOf("$ordinal").foundOr(Int.MIN_VALUE)
        )

    fun part2(input: List<String>): Int {
        return input.sumOf { string ->
            val first = Digit.entries.map { digit ->
                Pair(digit, digit.findFirstIndex(string))
            }.filter { it.second >= 0 }.minBy { it.second }.first

            val last = Digit.entries.map { digit ->
                Pair(digit, digit.findLastIndex(string))
            }.filter { it.second >= 0 }.maxBy { it.second }.first

            "${first.ordinal}${last.ordinal}".toInt()
        }
    }

    // test if implementation meets criteria from the description, like:
    val part1TestInput = readInput("Day01_test")
    assertThat(part1(part1TestInput)).isEqualTo(142)

    val part2TestInput = readInput("Day01_test2")
    assertThat(part2(part2TestInput)).isEqualTo(281)

    val input = readInput("Day01")
    println("Part 1:")
    measureTime {
        part1(input).println()
    }.println()
    println()


    println("Part 2:")
    measureTime {
        part2(input).println()
    }.println()
}
