import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Update(
    @SerialName("update_id")
    val updateId: Long,
    @SerialName("message")
    val message: Message? = null,
    @SerialName("callback_query")
    val callbackQuery: CallbackQuery? = null,
)

@Serializable
data class Response(
    @SerialName("result")
    val result: List<Update>
)

@Serializable
data class Message(
    @SerialName("text")
    val text: String,
    @SerialName("chat")
    val chat: Chat,
)

@Serializable
data class CallbackQuery(
    @SerialName("data")
    val data: String,
    @SerialName("message")
    val message: Message? = null
)

@Serializable
data class Chat(
    @SerialName("id")
    val id: Long
)

@Serializable
data class SendMessageRequest(
    @SerialName("chat_id")
    val chatId: Long,
    @SerialName("text")
    val text: String,
    @SerialName("reply_markup")
    val replyMarkup: ReplyMarkup,
)

@Serializable
data class ReplyMarkup(
    @SerialName("inline_keyboard")
    val inlineKeyboard: List<List<InlineKeyboard>>,
)

@Serializable
data class InlineKeyboard(
    @SerialName("callback_data")
    val callbackData: String,
    @SerialName("text")
    val text: String,
)


fun main(args: Array<String>) {

    val telegramBotService = TelegramBotService(args[0])
    var lastUpdateId = 0L
    val trainers = HashMap<Long, LearnWordsTrainer>()



    while (true) {
        Thread.sleep(500)
        val response: Response = telegramBotService.getUpdates(lastUpdateId)
        println(response)
        if (response.result.isEmpty()) continue
        val sortedUpdates = response.result.sortedBy { it.updateId }
        sortedUpdates.forEach { handleUpdate(it, trainers, telegramBotService) }
        lastUpdateId = sortedUpdates.last().updateId + 1
    }
}

fun handleUpdate(
    update: Update,
    trainers: HashMap<Long, LearnWordsTrainer>,
    service: TelegramBotService
) {

    val message = update.message?.text
    val chatId = update.message?.chat?.id ?: update.callbackQuery?.message?.chat?.id ?: return
    val data = update.callbackQuery?.data

    val trainer = trainers.getOrPut(chatId) { LearnWordsTrainer(nameOfFileDictionary = "$chatId.txt") }
    if (message?.lowercase() == "/start") {
        service.sendMenu(chatId)
    }

    if (data?.lowercase() == CALLBACKS.LEARN_WORDS_CLICKED.callback) {
        checkNextQuestionAndSend(service, trainer, chatId)
    }

    if (data?.lowercase() == CALLBACKS.RESET_CLICKED.callback) {
        trainer.resetProgress()
        service.sendMessage(
            chatId,
            "Прогресс успешно сброшен"
        )
        service.sendMenu(chatId)
    }

    if (data?.lowercase() == CALLBACKS.STATISTICS_CLICKED.callback) {
        val statistics = trainer.getStatistic()
        service.sendMessage(
            chatId,
            "Выучено ${statistics.learned} из ${statistics.total} слов | ${statistics.percent}%"
        )
    }

    if (data?.startsWith(CALLBACKS.CALLBACK_DATA_ANSWER_PREFIX.callback) == true) {
        val index = data.substringAfter(CALLBACKS.CALLBACK_DATA_ANSWER_PREFIX.callback).toIntOrNull()
        if (trainer.checkAnswer(index)) {
            service.sendMessage(chatId, "Правильно!")
        } else {
            service.sendMessage(
                chatId,
                "${trainer.question?.correctAnswer?.englishWord} - ${trainer.question?.correctAnswer?.russianWord}"
            )
        }
        checkNextQuestionAndSend(service, trainer, chatId)
    }
}

fun checkNextQuestionAndSend(
    service: TelegramBotService,
    trainer: LearnWordsTrainer,
    chatId: Long,
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