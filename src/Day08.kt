fun main() {
    fun challengeInput(input: List<String>): Pair<CamelInstructions, List<CamelPosition>> {
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
        return Pair(instructions, camels)
    }

    fun part1(input: List<String>): Long {
        val (instructions, camels) = challengeInput(input)
        val camelRun = CamelRun(camels)

        return camelRun.executeInstructions(instructions, listOf("AAA")) { camelPositions ->
            camelPositions.all {
                it.identifier == "ZZZ"
            }
        }
    }

    fun part2(input: List<String>): Long {
        val (instructions, camels) = challengeInput(input)
        val camelRun = CamelRun(camels)
        return camelRun.executeInstructions(instructions)
    }

    run("Day08".createFiles(), ::part1, ::part2, 6L, 6L)
}

private class CamelRun(camels: List<CamelPosition>) {
    val camelsMap = camels.associateBy { it.identifier }

    fun executeInstructions(
        camelInstructions: CamelInstructions,
        starts: List<String>,
        reachedEnd: (List<CamelPosition>) -> Boolean
    ): Long {

        starts.map { camelsMap[it] ?: throw IllegalArgumentException("Missing final step $it") }


        var current = starts.map { camelsMap[it] ?: throw IllegalArgumentException("Missing final step $it") }
        var steps = 0L

        generateSequence { camelInstructions.order() }
            .takeWhile { !reachedEnd(current) }
            .forEach {
                it.takeWhile { !reachedEnd(current) }
                    .forEach { step ->
                        current = current.map { camelPosition ->
                            camelsMap[step(camelPosition)] ?: throw IllegalStateException("Invalid position")
                        }
                        steps++
                    }
            }

        return steps
    }


    fun executeInstructions(
        camelInstructions: CamelInstructions
    ): Long {

        fun step(start: String) = camelInstructions.instructions.fold(start) { acc, char ->
            when (char) {
                'L' -> camelsMap[acc]!!.left
                'R' -> camelsMap[acc]!!.right
                else ->
                    @Suppress("ThrowingExceptionsWithoutMessageOrCause", "UseCheckOrError")
                    throw IllegalStateException()
            }
        }

        return camelInstructions.instructions.length *
                camelsMap.keys.filter { it.endsWith('A') }.fold(1L) { acc, start ->

                    val (index, end) = generateSequence(start, ::step).withIndex()
                        .first { (_, end) -> end.endsWith('Z') }
                    check(step(start) == step(end)) { "required for lcm solution" }
                    findLCM(acc, index.toLong())
                }
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
