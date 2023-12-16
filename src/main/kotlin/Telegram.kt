import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

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

    val trainer = LearnWordsTrainer()
    val telegramBotService = TelegramBotService(args[0])
    var lastUpdateId = 0L

    val json = Json {
        ignoreUnknownKeys = true
    }

    while (true) {
        Thread.sleep(500)
        val responseString: String = telegramBotService.getUpdates(lastUpdateId)
        println(responseString)
        val response: Response = json.decodeFromString(responseString)
        val updates = response.result
        val firstUpdate = updates.firstOrNull() ?: continue
        val updateId = firstUpdate.updateId
        lastUpdateId = updateId + 1

        val message = firstUpdate.message?.text
        val chatId = firstUpdate.message?.chat?.id ?: firstUpdate.callbackQuery?.message?.chat?.id
        val data = firstUpdate.callbackQuery?.data

        if (message?.lowercase() == "/start" && chatId != null) {
            telegramBotService.sendMenu(chatId, json)
        }

        if (data?.lowercase() == CALLBACKS.LEARN_WORDS_CLICKED.callback && chatId != null) {
            checkNextQuestionAndSend(telegramBotService, trainer, chatId, json)
        }

        if (data?.lowercase() == CALLBACKS.STATISTICS_CLICKED.callback && chatId != null) {
            val statistics = trainer.getStatistic()
            telegramBotService.sendMessage(
                chatId,
                "Выучено ${statistics.learned} из ${statistics.total} слов | ${statistics.percent}%"
            )
        }

        if (data?.startsWith(CALLBACKS.CALLBACK_DATA_ANSWER_PREFIX.callback) == true && chatId != null) {
            val index = data.substringAfter(CALLBACKS.CALLBACK_DATA_ANSWER_PREFIX.callback).toIntOrNull()
            if (trainer.checkAnswer(index)) {
                telegramBotService.sendMessage(chatId, "Правильно!")
            } else {
                telegramBotService.sendMessage(
                    chatId,
                    "${trainer.question?.correctAnswer?.englishWord} - ${trainer.question?.correctAnswer?.russianWord}"
                )
            }
            checkNextQuestionAndSend(telegramBotService, trainer, chatId, json)
        }
    }
}

fun checkNextQuestionAndSend(
    service: TelegramBotService,
    trainer: LearnWordsTrainer,
    chatId: Long,
    json: Json,
): Question? {
    val question = trainer.getNextQuestion()
    return if (question == null) {
        service.sendMessage(chatId, "Вы выучили все слова в базе")
        null
    } else {
        service.sendQuestion(chatId, question, json)
        question
    }
}