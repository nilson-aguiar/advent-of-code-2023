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

    run("Day01", ::part1, ::part2, 142, 281)
}
