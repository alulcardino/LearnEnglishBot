fun Question.asConsoleString(): String {
    val options = this.options.mapIndexed { index, word ->
        "${index + 1}. ${word.russianWord}\n"
    }.joinToString("")
    return "${this.correctAnswer.englishWord}?\n${options}\n0. Назад в меню"
}


fun main() {
    val trainer = LearnWordsTrainer(5, "dictionary.txt", 3)
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
                    val userAnswer = readln().toIntOrNull()
                    if (userAnswer == 0) break


                    if (trainer.checkAnswer(userAnswer?.minus(1))) {
                        println("Правильно!")
                    } else {
                        println("Неправильно! ${question.correctAnswer.englishWord} - это ${question.correctAnswer.russianWord}")
                    }
                }
            }
            2 -> {
                val statistics = trainer.getStatistic()
                println("Выучено ${statistics.learned} из ${statistics.total} слов | ${statistics.percent}%")

            }
            0 -> break
        }
    }
}