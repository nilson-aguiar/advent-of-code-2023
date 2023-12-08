fun main() {

    fun mapSchema(input: List<String>): Schema {
        val parts = mutableListOf<Part>()
        val symbols = mutableListOf<Symbol>()

        input.forEachIndexed { y, line ->

            var x = 0
            while (x <= line.lastIndex) {
                val curr = line[x]
                if (curr.isDigit()) {
                    val number = line.drop(x).takeWhile { it.isDigit() }.toInt()
                    Part(number, y, x).run {
                        parts += this
                        x += part.toString().length
                    }
                } else {
                    if (curr != '.') {
                        symbols += Symbol(curr, Position(y, x))
                    }
                    x++
                }
            }
        }

        return Schema(parts, symbols)
    }

    fun part1(input: List<String>): Int {

        val (parts, symbols) = mapSchema(input)

        val grouppedSymbols = symbols.groupBy { it.position.row }

        return parts.filter { part ->
            val result = part.rangeForSymbol().flatten().firstNotNullOfOrNull { position ->
                grouppedSymbols[position.row]?.firstNotNullOfOrNull {
                    if (it.position == position) {
                        it
                    } else null
                }
            }
            result != null
        }.sumOf { it.part }
    }

    fun part2(input: List<String>): Int {
        val (parts, symbols) = mapSchema(input)


        val grouppedParts = parts.groupBy { it.row }

        return symbols
            .filter { it.part == '*' }
            .map { symbol ->
                symbol.position.rangeForAdjacent(1).flatten().mapNotNull { position ->
                    grouppedParts[position.row]?.filter { it.positions().contains(position) } ?: emptyList()
                }.filter { it.isNotEmpty() }.flatten().toSet()
                    .map { it.part }
            }
            .filter {
                it.size == 2
            }.sumOf {
                it.reduce { acc, part -> acc * part }
            }
    }

    run("Day03".createFiles(), ::part1, ::part2, 4361, 467835)
}

private data class Schema(
    val parts: List<Part>,
    val symbols: List<Symbol>
)

private data class Position(
    val row: Int,
    val rowPosition: Int
) {
    fun rangeForAdjacent(lenght: Int) =
        listOf(row - 1, row, row + 1)
            .map { row ->
                (rowPosition - 1..rowPosition + lenght).mapNotNull { position ->
                    if (row >= 0 && position >= 0) {
                        Position(row, position)
                    } else null
                }
            }.filter { it.isNotEmpty() }


}

private data class Symbol(
    val part: Char,
    val position: Position
) {
    init {
        assert(!part.isDigit())
    }
}

private data class Part(
    val part: Int,
    val row: Int,
    val rowPosition: Int
) {
    private val position = Position(row, rowPosition)

    fun rangeForSymbol() = position.rangeForAdjacent(part.toString().length)
    fun positions() = (rowPosition..<rowPosition + part.toString().length).map { Position(row, it) }
}