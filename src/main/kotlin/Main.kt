import java.io.File

const val LEARNED_WORDS_CONDITION_THRESHOLD = 3

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
    val learnedWords = words.filter {
        it.correctAnswersCount >= LEARNED_WORDS_CONDITION_THRESHOLD
    }
    val percent = (learnedWords.size.toDouble() / words.size) * 100
    println("Выучено ${learnedWords.size} из ${words.size} слов | $percent%")
}

fun getUnlearnedWords(words: MutableList<Word>) {
    while (true) {
        val unlearnedWords = words.filter {
            it.correctAnswersCount <= LEARNED_WORDS_CONDITION_THRESHOLD
        }
        if (unlearnedWords.isEmpty()) {
            println("Вы выучили все слова")
            return
        }
        val shuffledWords = unlearnedWords.shuffled().take(4)
        val rightWord = shuffledWords.random()
        val rightWordIndex = shuffledWords.indexOf(rightWord)
        val options = shuffledWords.mapIndexed { index, word ->
            "${index + 1}. ${word.russianWord}\n"
        }.joinToString("")

        println("${rightWord.englishWord}?\n$options\n0. Назад в меню")
        when (readln().toIntOrNull()) {
            0 -> return
            rightWordIndex + 1 -> {
                println("Правильно!")
                words[unlearnedWords.indexOf(rightWord)].correctAnswersCount++
                saveDictionary(words)
            }

            else -> return
        }
    }
}

fun saveDictionary(dictionary: List<Word>) {
    val wordsFile = File("dictionary.txt")
    wordsFile.writeText("")
    for (word in dictionary) {
        wordsFile.appendText("${word.englishWord}|${word.russianWord}|${word.correctAnswersCount}\n")
    }
}

data class Word(
    val englishWord: String,
    val russianWord: String,
    var correctAnswersCount: Int,
) {
    override fun toString(): String {
        return "Word(englishWord='$englishWord', russianWord='$russianWord', correctAnswersCount=$correctAnswersCount)"
    }
}