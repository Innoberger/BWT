import java.util.*
import java.util.stream.IntStream
import kotlin.collections.HashSet

fun main() {
    println("BANANENANBAU".encodeWithBWT().also { println(it) }.decode())
}

fun String.rotate(distance: Int) =
    toList().also { text ->
        Collections.rotate(text, distance)
    }.joinToString("")

fun String.encodeWithBWT() = BWT.encode(this)

data class BWT(val encodedText: String, val originalIndex: Int) {
    companion object {
        fun encode(text: String): BWT {
            val matrix: List<String> = text
                .mapIndexed { i, _ -> text.rotate(i) }
                .sorted()

            val index: Int = matrix.indexOf(text)
            val encoded: String = matrix.map { it[matrix.lastIndex] }.joinToString("")

            return BWT(encoded, index)
        }
    }

    fun decode(printMatrix: Boolean = false): String {
        val matrix: List<CharArray> = List(encodedText.length) { CharArray(encodedText.length) }

        // initialize the matrix by setting first and last column
        encodedText.indices.forEach { charIndex ->
            matrix[charIndex][0] = encodedText.toList().sorted()[charIndex]
            matrix[charIndex][encodedText.lastIndex] = encodedText[charIndex]
        }

        // iterate through the matrix from second column until the second last column
        IntStream.range(0, encodedText.lastIndex).forEach { colIndex ->
            var usedIndices: Set<Int> = HashSet()
            // iterate through every matrix row
            IntStream.rangeClosed(0, encodedText.lastIndex).forEach { rowIndex ->
                val findRowIndex = (matrix.indices.minus(usedIndices)).find { findRowIndex ->
                    matrix[rowIndex].joinToString("").substring(0, colIndex + 1) == matrix[findRowIndex].joinToString("").rotate(1).substring(0, colIndex + 1)
                } ?: throw NullPointerException()

                usedIndices.plus(findRowIndex).also { usedIndices = it }

                matrix[rowIndex][colIndex + 1] = matrix[findRowIndex][colIndex]
            }
        }

        // print only for debugging
        if (printMatrix) matrix.indices.forEach { row ->
            println(matrix[row].joinToString(" "))
        }

        return matrix[originalIndex].joinToString("")
    }

    override fun toString(): String {
        return "${encodedText}, $originalIndex"
    }
}