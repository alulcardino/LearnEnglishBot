import java.io.File

fun main() {

    val wordsFile: File = File("src/main/kotlin/dictionary.txt")
    println(wordsFile.readLines())
}