import java.io.File

fun main() {
    val listOfWord = mutableListOf<Word>()
    val wordsFile  = File("dictionary.txt")
    val words = wordsFile.readLines()
    for (string in words) {
        val splitedString = string.split("|")
        if (splitedString.size == 3) {
            listOfWord.add(Word(splitedString[0], splitedString[1], splitedString[2].toIntOrNull() ?: 0))
        }
    }
    while (true) {
        println("Меню: 1 – Учить слова, 2 – Статистика, 0 – Выход")
        when (readln().toIntOrNull() ?: 0) {
            1 -> true
            2 -> getStatistic(listOfWord)
            0 -> break
        }
    }
}

fun getStatistic(words: MutableList<Word>) {
    val statistic = words.filter {
        it.correctAnswersCount >= 3
    }
    val percent = (statistic.size.toDouble() / words.size) * 100
    println("Выучено ${statistic.size} из ${words.size} слов | $percent%")
}

data class Word(
    private val englishWord: String,
    private val russianWord: String,
    val correctAnswersCount: Int,
) {
    override fun toString(): String {
        return "Word(englishWord='$englishWord', russianWord='$russianWord', correctAnswersCount=$correctAnswersCount)"
    }
}