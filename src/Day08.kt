fun main() {
    fun challengeInput(
        input: List<String>,
        findStart: (CamelPosition) -> Boolean,
        reachedEnd: (CamelPosition) -> Boolean
    ): Long {
        val instructions = CamelInstructions(input.first())

        val camels = input.drop(2).map { line ->
            line.split("=")
                .map { it.trim() }
                .zipWithNext()
                .map { (index, positions) ->
                    val (left, right) = positions.replace("(", "")
                        .replace(")", "")
                        .split(",")
                        .map { it.trim() }
                        .zipWithNext().first()

                    CamelPosition(index, left, right)
                }
        }.flatten()

        return CamelRun(camels)
            .executeInstructions(instructions, camels.filter(findStart), reachedEnd)
            .lcm()
    }

    fun part1(input: List<String>): Long =
        challengeInput(
            input,
            findStart = { it.identifier == "AAA" },
            reachedEnd = { it.identifier == "ZZZ" }
        )

    fun part2(input: List<String>): Long =
        challengeInput(
            input,
            findStart = { it.identifier.endsWith('A') },
            reachedEnd = { it.identifier.endsWith('Z') }
        )

    run("Day08".createFiles(), ::part1, ::part2, 6L, 6L)
}

private class CamelRun(camels: List<CamelPosition>) {
    val camelsMap = camels.associateBy { it.identifier }

    fun executeInstructions(
        camelInstructions: CamelInstructions,
        starts: List<CamelPosition>,
        reachedEnd: (CamelPosition) -> Boolean
    ): List<Long> =
        starts.map { start ->
            var current = start
            var steps = 0L

            generateSequence { camelInstructions.order() }
                .flatten()
                .takeWhile { !reachedEnd(current) }
                .forEach { step ->
                    current = camelsMap[step(current)] ?: throw IllegalStateException("Invalid position")
                    steps++
                }

            steps
        }
}

private data class CamelPosition(val identifier: String, val left: String, val right: String)

private data class CamelInstructions(val instructions: String) {
    fun right(camelPosition: CamelPosition) = camelPosition.right
    fun left(camelPosition: CamelPosition) = camelPosition.left

    fun order(): List<(CamelPosition) -> String> {
        return (0..instructions.lastIndex).map { instructions[it] }.map { c ->
            when (c) {
                'R' -> ::right
                'L' -> ::left
                else -> throw IllegalArgumentException("Invalid instruction [$c]")
            }
        }
    }
}
