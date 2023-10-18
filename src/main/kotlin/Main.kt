import java.io.File

fun main() {
    val listOfWord = mutableListOf<Word>()
    val wordsFile: File = File("dictionary.txt")
    val words = wordsFile.readLines()
    for (string in words) {
        val splitedString = string.split("|")
        listOfWord.add(Word(splitedString[0], splitedString[1], splitedString[1].toInt() ?: 0))
    }
    listOfWord.forEach { println(it) }
}

data class Word(
    private val englishWord: String,
    private val russianWord: String,
    private val correctAnswersCount: Int?,
) {
    override fun toString(): String {
        return "Word(englishWord='$englishWord', russianWord='$russianWord', correctAnswersCount=$correctAnswersCount)"
    }
}