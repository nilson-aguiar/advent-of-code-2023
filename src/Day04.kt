import kotlin.math.min

fun main() {
    fun part1(input: List<String>): Int {
        fun doubleIt(value: Int) = value * 2

        return cards(input, ::doubleIt).sum()
    }

    fun part2(input: List<String>): Int {

        fun sumIt(value: Int) = value + 1

        val cardsResult = cards(input, ::sumIt).toMutableList()
        val cardCount = mutableMapOf<Int, Int>()

        cardsResult.forEachIndexed { index, _-> cardCount[index] = 1 }
        cardsResult.mapIndexed { index, i ->
            repeat(cardCount[index]!!) {
                var amount = i
                while(amount > 0) {
                    val forIndex = min(index + amount, cardsResult.lastIndex)

                    if(forIndex <= cardsResult.size) {
                        cardCount[forIndex] = cardCount[forIndex]!! + 1
                    }
                    amount--
                }
            }
        }

        cardCount.println()

        return cardCount.values.sum()
    }

    run("Day04".createFiles(), ::part1, ::part2, 13, 30)
}

private fun cards(input: List<String>, calculation: (Int) -> Int) = input.map { line ->
    line.dropWhile { it != ':' }.drop(2)
        .split("|")
        .map { numbers ->
            numbers.split(" ")
                .filter { it.isNotBlank() }
                .map { value -> value.toInt() }

        }
        .zipWithNext()
        .maxOf { (winning, values) ->
            var lastFound = 0
            val sortedWinningNumber = winning.sorted()
            values.sorted().fold(0) { acc, value ->
                sortedWinningNumber.binarySearch(value, fromIndex = lastFound).let {
                    if (it >= 0) {
                        lastFound = it
                        if (acc == 0) {
                            1
                        } else calculation.invoke(acc)
                    } else acc
                }
            }
        }

}
