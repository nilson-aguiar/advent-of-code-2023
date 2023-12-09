import org.assertj.core.api.Assertions
import java.math.BigInteger
import java.nio.file.FileAlreadyExistsException
import java.security.MessageDigest
import kotlin.io.path.Path
import kotlin.io.path.createFile
import kotlin.io.path.readLines
import kotlin.time.measureTime

/**
 * Reads lines from the given input txt file.
 */
fun readInput(name: String) = Path("src/resources/$name.txt").readLines()

/**
 * Converts string to md5 hash.
 */
fun String.md5() = BigInteger(1, MessageDigest.getInstance("MD5").digest(toByteArray()))
    .toString(16)
    .padStart(32, '0')


fun <T> Collection<T>?.println() = this?.forEach { it.println() } ?: println("null")
/**
 * The cleaner shorthand for printing output.
 */
fun Any?.println() = println(this)

fun <T> run(day:String,
            part1: (input: List<String>) -> T, part2: (input: List<String>) -> T,
            expectedResult: T, expectedResult2: T) {
    // test if implementation meets criteria from the description, like:
    val part1TestInput = readInput("${day}_test")
    Assertions.assertThat(part1(part1TestInput)).isEqualTo(expectedResult)

    val part2TestInput = readInput("${day}_test2")
    Assertions.assertThat(part2(part2TestInput)).isEqualTo(expectedResult2)

    val input = readInput(day)
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

fun Sequence<Int>.product(): Int =
    this.reduce { acc, i -> acc * i }

fun String.createFiles(): String {
    try { Path("src/resources/$this.txt").createFile() } catch (ignored: FileAlreadyExistsException) { /* Ignored */ }
    try { Path("src/resources/${this}_test.txt").createFile() } catch (ignored: FileAlreadyExistsException) { /* Ignored */ }
    try { Path("src/resources/${this}_test2.txt").createFile() } catch (ignored: FileAlreadyExistsException) { /* Ignored */ }

    return this
}

fun findLCM(a: Long, b: Long): Long {
    val larger = if (a > b) a else b
    val maxLcm = a * b
    var lcm = larger
    while (lcm <= maxLcm) {
        if (lcm % a == 0L && lcm % b == 0L) {
            return lcm
        }
        lcm += larger
    }
    return maxLcm
}