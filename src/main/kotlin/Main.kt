import java.io.File

fun main() {

    val wordsFile: File = File("dictionary.txt")
    val words = wordsFile.readLines()
    for (element in words) {
        println(element)
    }
}