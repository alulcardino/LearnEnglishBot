import java.net.URI
import java.net.URLEncoder
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.nio.charset.StandardCharsets

const val START_URL = "https://api.telegram.org/bot"



enum class CALLBACKS(val callback: String) {
    STATISTICS_CLICKED("statistics_clicked"),
    LEARN_WORDS_CLICKED("learn_words_clicked"),
    CALLBACK_DATA_ANSWER_PREFIX("answer_")
}

class TelegramBotService {

    private val builder: HttpClient.Builder = HttpClient.newBuilder()
    private val client: HttpClient = builder.build()

    fun getUpdates(token: String, updateId: Int): String {
        val urlGetUpdate = "$START_URL$token/getUpdates?offset=$updateId"

        val request: HttpRequest = HttpRequest.newBuilder().uri(URI.create(urlGetUpdate)).build()

        val response: HttpResponse<String> = client.send(request, HttpResponse.BodyHandlers.ofString())
        return response.body()
    }

    fun sendMessage(token: String, chatId: String?, text: String?): String {
        val encoded = URLEncoder.encode(
            text,
            StandardCharsets.UTF_8
        )
        val urlSendMessage = "$START_URL$token/sendMessage?chat_id=$chatId&text=$encoded"

        val request: HttpRequest = HttpRequest.newBuilder().uri(URI.create(urlSendMessage)).build()

        val response: HttpResponse<String> = client.send(request, HttpResponse.BodyHandlers.ofString())
        return response.body()
    }

    fun sendQuestion(token: String, chatId: String?, question: Question) : String{
        val urlSendMessage = "$START_URL$token/sendMessage?chat_id=$chatId"
        val sendQuestionBody = """
            {
                "chat_id": $chatId,
                "text": "${question.correctAnswer.englishWord}",
                "reply_markup": {
                    "inline_keyboard": [
                        [
                              ${question.asJsonString()}
                        ]
                    ]
                }
            }
        """.trimIndent()
        println(sendQuestionBody)

        val request: HttpRequest = HttpRequest.newBuilder().uri(URI.create(urlSendMessage))
            .header("Content-type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(sendQuestionBody))
            .build()

        val response: HttpResponse<String> = client.send(request, HttpResponse.BodyHandlers.ofString())
        return response.body()
    }

    fun sendMenu(token: String, chatId: String?): String {
        val urlSendMessage = "$START_URL$token/sendMessage?chat_id=$chatId"
        val sendMenuBody = """
            {
                "chat_id": $chatId,
                "text": "Основное меню",
                "reply_markup": {
                    "inline_keyboard": [
                        [
                            {
                                 "text": "Изучить слова",
                                 "callback_data": "${CALLBACKS.LEARN_WORDS_CLICKED.name}"
                            },
                            {
                                 "text": "Статистика",
                                 "callback_data": "${CALLBACKS.STATISTICS_CLICKED.name}"
                            }
                        ]
                    ]
                }

            }
        """.trimIndent()

        val request: HttpRequest = HttpRequest.newBuilder().uri(URI.create(urlSendMessage))
            .header("Content-type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(sendMenuBody))
            .build()

        val response: HttpResponse<String> = client.send(request, HttpResponse.BodyHandlers.ofString())
        return response.body()
    }
}