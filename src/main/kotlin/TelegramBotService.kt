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
    RESET_CLICKED("reset_clicked"),
    CALLBACK_DATA_ANSWER_PREFIX("answer_")
}

class TelegramBotService(
    private val token: String
) {

    private val builder: HttpClient.Builder = HttpClient.newBuilder()
    private val client: HttpClient = builder.build()

    private val json = Json {
        ignoreUnknownKeys = true
    }

    fun getUpdates(updateId: Long): Response {
        val urlGetUpdate = "$START_URL$token/getUpdates?offset=$updateId"
        val request: HttpRequest = HttpRequest.newBuilder().uri(URI.create(urlGetUpdate)).build()

        val response: HttpResponse<String> = client.send(request, HttpResponse.BodyHandlers.ofString())

        return json.decodeFromString(response.body())
    }

    fun sendMessage(chatId: Long, text: String?): String {
        return runCatching {
            val encoded = URLEncoder.encode(
                text,
                StandardCharsets.UTF_8
            )
            val urlSendMessage = "$START_URL$token/sendMessage?chat_id=$chatId&text=$encoded"

            val request: HttpRequest = HttpRequest.newBuilder().uri(URI.create(urlSendMessage)).build()

            val response: HttpResponse<String> = client.send(request, HttpResponse.BodyHandlers.ofString())
            response.body()
        }.onFailure {
            "Problems with server"
        }.getOrDefault("request")
    }

    fun sendQuestion(chatId: Long, question: Question): String {
        return runCatching {
            val urlSendMessage = "$START_URL$token/sendMessage?chat_id=$chatId"
            val requestBody = SendMessageRequest(
                chatId = chatId,
                text = question.correctAnswer.englishWord,
                replyMarkup =
                ReplyMarkup(
                    listOf(question.options.mapIndexed { index, word ->
                        InlineKeyboard(
                            text = word.russianWord,
                            callbackData = "${CALLBACKS.CALLBACK_DATA_ANSWER_PREFIX.callback}$index"
                        )
                    }),
                )
            )
            val requestBodyString = json.encodeToString(requestBody)

            val request: HttpRequest = HttpRequest.newBuilder().uri(URI.create(urlSendMessage))
                .header("Content-type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestBodyString))
                .build()

            val response: HttpResponse<String> = client.send(request, HttpResponse.BodyHandlers.ofString())
            response.body()
        }.onFailure {
            "Problems with server"
        }.getOrDefault("request")
    }

    fun sendMenu(chatId: Long): String {
        return runCatching {

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
                            ),
                        ),
                        listOf(
                            InlineKeyboard(
                                text = "Сбросить прогресс",
                                callbackData = CALLBACKS.RESET_CLICKED.name
                            ),
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
            response.body()
        }.onFailure {
            "Problems with server"
        }.getOrDefault("request")
    }
}
