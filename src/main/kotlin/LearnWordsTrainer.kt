import java.io.File

const val LEARNED_WORDS_CONDITION_THRESHOLD = 3


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
    private val amountOfOptions: Int,
    private val nameOfFileDictionary: String,

    ) {

    private var question: Question? = null
    val dictionary = loadDictionary()

    fun getStatistic(words: List<Word>): Statistics {
        val learnedWords = words.filter {
            it.correctAnswersCount >= LEARNED_WORDS_CONDITION_THRESHOLD
        }
        val percent = learnedWords.size * 100 / words.size
        return Statistics(
            learnedWords.size,
            words.size,
            percent
        )
    }

    fun getNextQuestion(): Question? {
        val unlearnedWords = dictionary.filter {
            it.correctAnswersCount <= LEARNED_WORDS_CONDITION_THRESHOLD
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
                saveDictionary(dictionary)
                true
            } else {
                false
            }
        } ?: false
    }

    private fun saveDictionary(dictionary: List<Word>) {
        val wordsFile = File(nameOfFileDictionary)
        wordsFile.writeText("")
        for (word in dictionary) {
            wordsFile.appendText("${word.englishWord}|${word.russianWord}|${word.correctAnswersCount}\n")
        }
    }

    private fun loadDictionary(): List<Word> {
        val dictionary = mutableListOf<Word>()
        val wordsFile = File("dictionary.txt")
        wordsFile.readLines().forEach {
            val splitString = it.split("|")
            if (splitString.size == 3) {
                dictionary.add(Word(splitString[0], splitString[1], splitString[2].toIntOrNull() ?: 0))
            }
        }
        return dictionary
    }
}