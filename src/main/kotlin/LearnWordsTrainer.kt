import kotlinx.serialization.Serializable
import java.io.File

@Serializable
data class Word(
    val englishWord: String,
    val russianWord: String,
    var correctAnswersCount: Int,
) {
    override fun toString(): String {
        return "Word(englishWord='$englishWord', russianWord='$russianWord', correctAnswersCount=$correctAnswersCount)"
    }
}

const val NAME_OF_DICTIONARY_FILE = "dictionary.txt"

data class Statistics(
    val learned: Int,
    val total: Int,
    val percent: Int,
)

data class Question(
    val options: List<Word>,
    val correctAnswer: Word,
)

class LearnWordsTrainer(
    private val amountOfOptions: Int = 4,
    private val nameOfFileDictionary: String = NAME_OF_DICTIONARY_FILE,
    private val learnedWordsCondition: Int = 3,
) {

    var question: Question? = null

    fun getStatistic(): Statistics {
        val learnedWords = dictionary.filter {
            it.correctAnswersCount >= learnedWordsCondition
        }
        val percent = learnedWords.size * 100 / dictionary.size
        return Statistics(
            learnedWords.size,
            dictionary.size,
            percent
        )
    }

    fun getNextQuestion(): Question? {
        val unlearnedWords = dictionary.filter {
            it.correctAnswersCount <= learnedWordsCondition
        }
        if (unlearnedWords.isEmpty()) {
            return null
        }
        val shuffledWords = unlearnedWords.shuffled().take(amountOfOptions)
        val rightWord = shuffledWords.random()
        question = Question(
            shuffledWords,
            rightWord,
        )
        return question
    }

    fun checkAnswer(userAnswerIndex: Int?): Boolean {
        return question?.let {
            val correctAnswerIndex = it.options.indexOf(it.correctAnswer)
            if (userAnswerIndex == correctAnswerIndex) {
                it.correctAnswer.correctAnswersCount++
                saveDictionary()
                true
            } else {
                false
            }
        } ?: false
    }

    private fun saveDictionary() {
        val wordsFile = File(nameOfFileDictionary)
        wordsFile.writeText("")
        for (word in dictionary) {
            wordsFile.appendText("${word.englishWord}|${word.russianWord}|${word.correctAnswersCount}\n")
        }
    }

    private val dictionary = File(NAME_OF_DICTIONARY_FILE).readLines().mapNotNull {
        val splitString = it.split("|")
        if (splitString.size == 3) {
            Word(splitString[0], splitString[1], splitString[2].toIntOrNull() ?: 0)
        } else {
            null
        }
    }

    fun resetProgress() {
        dictionary.forEach { it.correctAnswersCount = 0 }
        saveDictionary()
    }

}