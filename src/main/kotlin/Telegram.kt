enum class TypeOfRegex(val regex: Regex) {
    UPDATE_ID("\"update_id\":([0-9]+)".toRegex()),
    CHAT_ID("\"id\":([0-9]+)".toRegex()),
    MESSAGE("\"text\":\"(.+?)\"".toRegex()),
    DATA("\"data\":\"(.+?)\"".toRegex()),
}

private fun fromJsonToValue(
    typeOfRegex: TypeOfRegex,
    updates: String,
): String? {
    val matchResult: MatchResult? = typeOfRegex.regex.find(updates)
    val groupsUpdate = matchResult?.groups
    return groupsUpdate?.get(1)?.value
}


fun main(args: Array<String>) {
    val trainer = LearnWordsTrainer()
    val telegramBotService = TelegramBotService(args[0])
    var updateId = 0

    while (true) {
        Thread.sleep(2000)
        val updates: String = telegramBotService.getUpdates(updateId)
        println(updates)

        val updateIdString = fromJsonToValue(TypeOfRegex.UPDATE_ID, updates) ?: continue
        updateId = updateIdString.toInt() + 1

        val chatId = fromJsonToValue(TypeOfRegex.CHAT_ID, updates)
        val message = fromJsonToValue(TypeOfRegex.MESSAGE, updates)
        val data = fromJsonToValue(TypeOfRegex.DATA, updates)
        if (message?.lowercase() == "menu" && chatId != null) {
            telegramBotService.sendMenu( chatId)
        }

        if (data?.lowercase() == CALLBACKS.STATISTICS_CLICKED.callback && chatId != null) {
            val statistics = trainer.getStatistic()
            telegramBotService.sendMessage(
                chatId,
                "Выучено ${statistics.learned} из ${statistics.total} слов | ${statistics.percent}%"
            )
        }
        if (trainer.question == null) {
            checkNextQuestionAndSend(telegramBotService, trainer, chatId)
        }
        if (data?.startsWith(CALLBACKS.CALLBACK_DATA_ANSWER_PREFIX.callback) == true) {
            val index = data.substringAfter(CALLBACKS.CALLBACK_DATA_ANSWER_PREFIX.callback).toIntOrNull()
            if (trainer.checkAnswer(index)) {
                telegramBotService.sendMessage(chatId, "Правильно!")
            } else {
                telegramBotService.sendMessage(
                    chatId,
                    "${trainer.question?.correctAnswer?.englishWord} - ${trainer.question?.correctAnswer?.russianWord}"
                )
            }
            checkNextQuestionAndSend(telegramBotService, trainer, chatId)
        }
    }
}

fun checkNextQuestionAndSend(
    service: TelegramBotService,
    trainer: LearnWordsTrainer,
    chatId: String?
): Question? {
    val question = trainer.getNextQuestion()
    return if (question == null) {
        service.sendMessage(chatId, "Вы выучили все слова в базе")
        null
    } else {
        service.sendQuestion(chatId, question)
        question
    }
}