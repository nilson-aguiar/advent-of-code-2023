import org.assertj.core.api.Assertions.assertThat

fun main() {
    fun part1(input: List<String>): Int {
        return input.sumOf {
            val first = it.firstNotNullOf { char -> char.digitToIntOrNull() }
            val last = it.reversed().firstNotNullOf { char -> char.digitToIntOrNull() }

            "$first$last".toInt()
        }
    }

    fun part2(input: List<String>): Int {
        return input.size
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day01_test")
    assertThat(part1(testInput)).isEqualTo(142)

    val input = readInput("Day01")
    part1(input).println()
    part2(input).println()
}
