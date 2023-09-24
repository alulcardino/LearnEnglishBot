import java.io.File

fun main() {

    val wordsFile: File = File("src/main/kotlin/dictionary.txt")
    val words = wordsFile.readLines()
    for (element in words) {
        println(element)
    }
}