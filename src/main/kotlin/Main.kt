import java.io.File
import java.lang.StringBuilder
import java.util.*

const val learned_words_condition_threashold = 3

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
        it.correctAnswersCount >= learned_words_condition_threashold
    }
    val percent = (learnedWords.size.toDouble() / words.size) * 100
    println("Выучено ${learnedWords.size} из ${words.size} слов | $percent%")
}

fun getUnlearnedWords(words: MutableList<Word>) {
    while (true) {
        val unlearnedWords = words.filter {
            it.correctAnswersCount <= learned_words_condition_threashold
        }
        if (unlearnedWords.isEmpty()) {
            println("Вы выучили все слова")
            return
        }
        val shuffledWords = unlearnedWords.shuffled().take(4)
        val rightWord = shuffledWords.random()
        val rightWordIndex = shuffledWords.indexOf(rightWord)
        val options = shuffledWords.mapIndexed{
                index, word ->
           "${index + 1}. ${word.russianWord}\n"
        }.joinToString("")

        println("${rightWord.englishWord}?\n$options\n0. Назад в меню")

        val answers = listOf(0, 1, 2, 3)
        when (readln().toIntOrNull()) {
            answers[0] -> if (answers[0] == rightWordIndex) {
                words[unlearnedWords.indexOf(rightWord)].correctAnswersCount++
                saveDictionary(words)
            }
            answers[1] -> if (answers[1] == rightWordIndex) {
                words[unlearnedWords.indexOf(rightWord)].correctAnswersCount++
                saveDictionary(words)
            }
            answers[2] -> if (answers[2] == rightWordIndex) {
                words[unlearnedWords.indexOf(rightWord)].correctAnswersCount++
                saveDictionary(words)
            }
            answers[3] -> if (answers[3] == rightWordIndex) {
                words[unlearnedWords.indexOf(rightWord)].correctAnswersCount++
                saveDictionary(words)
            }
            else -> return
        }
    }
}

fun saveDictionary(dictionary: List<Word>) {
    val wordsFile = File("dictionary.txt")
    for (word in dictionary) {
        wordsFile.writeText("${word.englishWord}|${word.russianWord}|${word.correctAnswersCount}")
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