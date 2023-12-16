import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.net.URI
import java.net.URLEncoder
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.nio.charset.StandardCharsets

const val START_URL = "https://api.telegram.org/bot"

fun Question.asJsonString(): String {
    val options = this.options.mapIndexed { index, word ->
        """[
                {
                    "text":"${index + 1}. ${word.russianWord}",
                    "callback_data":"${CALLBACKS.CALLBACK_DATA_ANSWER_PREFIX.callback}$index"
                }
          ]
        """.trimIndent()
    }.joinToString(",\n")
    return options
}

enum class CALLBACKS(val callback: String) {
    STATISTICS_CLICKED("statistics_clicked"),
    LEARN_WORDS_CLICKED("learn_words_clicked"),
    CALLBACK_DATA_ANSWER_PREFIX("answer_")
}

class TelegramBotService(
    private val token: String
) {

    private val builder: HttpClient.Builder = HttpClient.newBuilder()
    private val client: HttpClient = builder.build()

    fun getUpdates(updateId: Long): String {
        val urlGetUpdate = "$START_URL$token/getUpdates?offset=$updateId"

        val request: HttpRequest = HttpRequest.newBuilder().uri(URI.create(urlGetUpdate)).build()

        val response: HttpResponse<String> = client.send(request, HttpResponse.BodyHandlers.ofString())
        return response.body()
    }

    fun sendMessage(chatId: Long?, text: String?): String {
        val encoded = URLEncoder.encode(
            text,
            StandardCharsets.UTF_8
        )
        val urlSendMessage = "$START_URL$token/sendMessage?chat_id=$chatId&text=$encoded"

        val request: HttpRequest = HttpRequest.newBuilder().uri(URI.create(urlSendMessage)).build()

        val response: HttpResponse<String> = client.send(request, HttpResponse.BodyHandlers.ofString())
        return response.body()
    }

    fun sendQuestion(chatId: Long, question: Question, json: Json): String {
        val urlSendMessage = "$START_URL$token/sendMessage?chat_id=$chatId"
        val sendQuestionBody = """
            {
                "chat_id": $chatId,
                "text": "${question.correctAnswer.englishWord}",
                "reply_markup": {
                    "inline_keyboard": [
                              ${question.asJsonString()}
                    ]
                }
            }
        """.trimIndent()
        println(sendQuestionBody)
        val requestBody = SendMessageRequest(
            chatId = chatId,
            text = question.correctAnswer.englishWord,
            replyMarkup =
            ReplyMarkup(
                listOf(question.options.mapIndexed { index, word ->
                    InlineKeyboard(
                        text = word.russianWord,
                        callbackData = "${CALLBACKS.CALLBACK_DATA_ANSWER_PREFIX}$index"
                    )
                })
            )
        )
        val requestBodyString = json.encodeToString(requestBody)

        val request: HttpRequest = HttpRequest.newBuilder().uri(URI.create(urlSendMessage))
            .header("Content-type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(requestBodyString))
            .build()

        val response: HttpResponse<String> = client.send(request, HttpResponse.BodyHandlers.ofString())
        return response.body()
    }

    fun sendMenu(chatId: Long, json: Json): String {
        val urlSendMessage = "$START_URL$token/sendMessage?chat_id=$chatId"
        val requestBody = SendMessageRequest(
            chatId = chatId,
            text = "Основное меню",
            replyMarkup = ReplyMarkup(
                listOf(
                    listOf(
                        InlineKeyboard(
                            text = "Изучать слова",
                            callbackData = CALLBACKS.LEARN_WORDS_CLICKED.name
                        ),
                        InlineKeyboard(
                            text = "Статистика",
                            callbackData = CALLBACKS.STATISTICS_CLICKED.name
                        )
                    )
                )
            )
        )
        val requestBodyString = json.encodeToString(requestBody)

        val request: HttpRequest = HttpRequest.newBuilder().uri(URI.create(urlSendMessage))
            .header("Content-type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(requestBodyString))
            .build()

        val response: HttpResponse<String> = client.send(request, HttpResponse.BodyHandlers.ofString())
        return response.body()
    }
}