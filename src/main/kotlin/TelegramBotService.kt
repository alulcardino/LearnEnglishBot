import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

const val START_URL = "https://api.telegram.org/bot"

class TelegramBotService {

    private val builder: HttpClient.Builder = HttpClient.newBuilder()
    private val client: HttpClient = builder.build()

    fun getUpdates(token: String, updateId: Int): String {
        val urlGetUpdate = "$START_URL$token/getUpdates?offset=$updateId"

        val request: HttpRequest = HttpRequest.newBuilder().uri(URI.create(urlGetUpdate)).build()

        val response: HttpResponse<String> = client.send(request, HttpResponse.BodyHandlers.ofString())

        return response.body()
    }

    fun sendMessage(token: String, chatId: String?, text: String?) : String{
        val urlSendMessage = "$START_URL$token/sendMessage?chat_id=$chatId&text=$text"


        val request: HttpRequest = HttpRequest.newBuilder().uri(URI.create(urlSendMessage)).build()

        val response: HttpResponse<String> = client.send(request, HttpResponse.BodyHandlers.ofString())
        return response.body()
    }
}