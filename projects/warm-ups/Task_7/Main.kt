import java.io.File

fun main() {
    val fileName = "sequence.txt"

    try {
        val text = File(fileName).readText().trim()

        val result = findMaxChainLength(text)

        println("Наибольшая длина цепочки: $result")

    } catch (e: Exception) {
        println("Ошибка при чтении файла: ${e.message}")
    }
}

fun findMaxChainLength(text: String): Int {
    val regex = Regex("(KL|LK)")
    val chains = text.split(regex)

    var maxLength = 0
    for (chain in chains) {
        if (chain.length > maxLength) {
            maxLength = chain.length
        }
    }

    return maxLength
}