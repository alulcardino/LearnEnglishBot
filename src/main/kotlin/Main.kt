const val LEARNED_WORDS_CONDITION_THRESHOLD = 3

data class Word(
    val englishWord: String,
    val russianWord: String,
    var correctAnswersCount: Int,
) {
    override fun toString(): String {
        return "Word(englishWord='$englishWord', russianWord='$russianWord', correctAnswersCount=$correctAnswersCount)"
    }
}

fun Question.asConsoleString(): String {
    val options = this.options.mapIndexed { index, word ->
        "${index + 1}. ${word.russianWord}\n"
    }.joinToString("")
    return "${this.answer.englishWord}?\n${options}options\n0. Назад в меню"
}



fun main() {
    val trainer = LearnWordsTrainer()
    while (true) {
        println("Меню: 1 – Учить слова, 2 – Статистика, 0 – Выход")
        when (readln().toIntOrNull() ?: 0) {
            1 -> {
                while (true) {
                    val question = trainer.getNextQuestion()
                    if (question == null) {
                        println("Вы выучили все слова")
                        break
                    }

                    println(question.asConsoleString())

                    val userAnswer = readln().toIntOrNull()
                    if (userAnswer == 0) break


                    if (trainer.checkAnswer(userAnswer?.minus(1))) {
                        println("Правильно!")
                    } else {
                        println("Неправильно! ${question.answer.englishWord} - это ${question.answer.russianWord}")
                    }
                }
            }
            2 -> {
                val statistics = trainer.getStatistic(trainer.dictionary)
                println("Выучено ${statistics.learned} из ${statistics.total} слов | ${statistics.percent}%")

            }
            0 -> break
        }
    }
}