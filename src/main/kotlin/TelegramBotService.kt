import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

class TelegramBotService {

    fun getUpdates(token: String, updateId: Int): String {
        val urlGetUpdate = "https://api.telegram.org/bot$token/getUpdates?offset=$updateId"

        val builder = HttpClient.newBuilder()
        val client: HttpClient = builder.build()

        val request: HttpRequest = HttpRequest.newBuilder().uri(URI.create(urlGetUpdate)).build()

        val response: HttpResponse<String> = client.send(request, HttpResponse.BodyHandlers.ofString())

        return response.body()
    }

    fun sendMessage(token: String, chatId: String?, text: String?) {
        val urlSendMessage = "https://api.telegram.org/bot$token/sendMessage?chat_id=$chatId&text=$text"

        val builder = HttpClient.newBuilder()
        val client: HttpClient = builder.build()

        val request: HttpRequest = HttpRequest.newBuilder().uri(URI.create(urlSendMessage)).build()

        val response: HttpResponse<String> = client.send(request, HttpResponse.BodyHandlers.ofString())

    }
}