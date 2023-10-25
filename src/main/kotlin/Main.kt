import java.io.File

fun main() {
    val listOfWord = mutableListOf<Word>()
    val wordsFile = File("dictionary.txt")
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
            1 -> getUnlearnedWords(listOfWord)
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

fun getUnlearnedWords(words: MutableList<Word>) {
    while (true) {
        val unlearnedWords = words.filter {
            it.correctAnswersCount <= 3
        }
        if (unlearnedWords.size == 4) {
            println("Вы выучили все слова")
            return
        }
        val shuffledWords = unlearnedWords.shuffled().take(4)
        val rightWord = shuffledWords.random()
        println(
            """
        ${rightWord.englishWord}?
        1. ${shuffledWords[0].russianWord}
        2. ${shuffledWords[1].russianWord}
        3. ${shuffledWords[2].russianWord}
        4. ${shuffledWords[3].russianWord}
        0. Назад в меню
    """.trimIndent()
        )

        when (readln().toIntOrNull()) {
            1 -> true
            2 -> true
            3 -> true
            4 -> true
            else -> return
        }
    }
}

data class Word(
    val englishWord: String,
    val russianWord: String,
    val correctAnswersCount: Int,
) {
    override fun toString(): String {
        return "Word(englishWord='$englishWord', russianWord='$russianWord', correctAnswersCount=$correctAnswersCount)"
    }
}