data class Game(
    val gameName: String,
    val gameContent: GameCubes
) {
    companion object {
        operator fun invoke(colonSeparated: String): Game {
            val split = colonSeparated.split(":")


            return Game(split.first(), GameCubes(split.last()))
        }
    }
}

data class GameCubes(
    val red: Int,
    val green: Int,
    val blue: Int
) {
    operator fun compareTo(maxCubes: GameCubes): Int =
        if (red <= maxCubes.red && green <= maxCubes.green && blue <= maxCubes.blue) {
            -1
        } else 1


    companion object {
        operator fun invoke(rounds: String): GameCubes {
            val game = rounds.split(";").map { round ->
                round.split(",").map { ball ->
                    val ballInput = ball.trim().split(" ")

                    ballInput.last() to ballInput.first().toInt()
                }
                    .groupingBy { it.first }
                    .fold(0) { acc, elem -> if (acc > elem.second) acc else elem.second }
            }.reduce { map1, map2 ->
                (map1.asSequence() + map2.asSequence()).groupingBy {
                    it.key
                }.fold(0) { acc, elem -> if (acc > elem.value) acc else elem.value }
            }

            return GameCubes(
                red = game.getOrDefault("red", 0),
                green = game.getOrDefault("green", 0),
                blue = game.getOrDefault("blue", 0)
            )
        }
    }
}

fun main() {

    fun part1(input: List<String>): Int {
        val maxCubes = GameCubes(12, 13, 14)

        return input.map { gameData ->
            Game(gameData)
        }.mapIndexed { index, game ->
            if (game.gameContent <= maxCubes) {
                println(game)
                index + 1
            } else {
                println(game)
                null
            }
        }
            .filterNotNull()
            .sum()
    }

    fun part2(input: List<String>): Int {
        return input.map { gameData ->
            Game(gameData)
        }.sumOf {
            with(it.gameContent) {
                red * green * blue
            }
        }
    }

    run("Day02".createFiles(), ::part1, ::part2, 8, 2286)
}
