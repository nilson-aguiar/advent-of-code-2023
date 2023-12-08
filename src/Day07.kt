fun main() {
    data class Hand(val cards: List<Int>, val groups: List<Int>, val bid: Int)

    fun part1(input: List<String>): Int =
        input.map { line ->
            line.split(" ").let {
                CardGame(it.first().sorted(), it.last().toInt())
            }
        }.sortedWith(
            compareBy(
                { it.highestResult.first.weight },
                { it.highestResult.second?.weight },
                { it.sortedCardsByValue[0] },
                { it.sortedCardsByValue[1] },
                { it.sortedCardsByValue[2] },
                { it.sortedCardsByValue[3] },
                { it.sortedCardsByValue[4] })
        )
            .mapIndexed { index, hand -> (index + 1) * hand.bid }
            .sum()

    fun part2(input: List<String>): Int =
        input.map { line ->
            line.split(" ").let {
                CardGame(it.first().sorted(), it.last().toInt())
            }
        }.sortedWith(
            compareBy(
                { it.highestResultJoker().first },
                { it.highestResultJoker().second },
                { it.sortedCardsByValue[0] },
                { it.sortedCardsByValue[1] },
                { it.sortedCardsByValue[2] },
                { it.sortedCardsByValue[3] },
                { it.sortedCardsByValue[4] })
        )
            .mapIndexed { index, hand -> (index + 1) * hand.bid }
            .sum()

    run("Day07".createFiles(), ::part1, ::part2, 6440, 5905)
}

private fun String.sorted() = String(toCharArray().apply { sort() })

private data class CardGame(val hand: String, val bid: Int) {

    private val chars: List<Int> = hand.map {
        NonDigitCards.getValueFor(it)
    }.toList()

    val sortedCardsByValue: List<Int> = hand.map {
        NonDigitCards.getValueFor(it)
    }.toList().sortedDescending()

    private val charSet = chars.toSet()


    private val countOf = chars.groupingBy { it }.eachCount()
    private val sortedCount = countOf.entries.sortedBy { it.value }


    private val first = countOf.toList().first()
    private val second = countOf.filter { it.value == 2 }.toList()
        .sortedBy { it.first }
        .getOrNull(1)

    val highestResult: Pair<HandResult, HandResult?> =
        when (charSet.size) {
            1 -> HandResult.FiveOf to null
            2 -> {
                if (sortedCount.first().value == 3)
                    HandResult.FullHouse to null
                else HandResult.FourOf to null
            }

            3 -> {
                if (countOf.values.max() == 3) {
                    HandResult.ThreeOf to second?.let { HandResult.TwoPair }
                } else HandResult.TwoPair to second?.let { HandResult.TwoPair } //TODO: 2x TwoPair
            }

            4 -> HandResult.TwoPair to null
            else -> HandResult.HighCard to null
        }

    fun highestResultJoker(): Pair<Int, Int?> {
        val joker = countOf.getOrDefault(NonDigitCards.J.value, 0)

        val sorted = countOf
            .filter { it.key != NonDigitCards.J.value }
            .map {
                it.key to it.value + joker
            }.sortedBy { it.second }

        return (sorted.getOrNull(0)?.second ?: joker) to sorted.getOrNull(1)?.second
    }
}

private enum class NonDigitCards(val value: Int) {
    T(10), J(11), Q(12), K(13), A(14);

    companion object {

        private val chars = entries.map { it.name.first() to it.value }

        fun getValueFor(char: Char): Int = char.digitToIntOrNull()
            ?: chars.first { it.first == char }.second
    }

}

private enum class HandResult(val weight: Int) {
    FiveOf(120), FourOf(100), FullHouse(80),
    ThreeOf(60), TwoPair(40), OnePair(20), HighCard(0);
}