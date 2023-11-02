import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

fun main(args: Array<String>) {
    val token = args[0]
    var updateId = 0

    while (true) {
        Thread.sleep(2000)
        val updates: String = getUpdates(token, updateId)
        println(updates)
        val startUpdateId = updates.lastIndexOf("update_id")
        val endUpdateId = updates.lastIndexOf(",\n\"message\"")
        if (startUpdateId == -1 || endUpdateId == -1) continue
        val updateIdString = updates.substring(startUpdateId + 11, endUpdateId)
        println(updateIdString)

        updateId = updateIdString.toInt() + 1
    }

}

fun getUpdates(token: String, updateId: Int): String {
  // val urlGetMe = "https://api.telegram.org/bot$token/getMe?offset=$updateId"
    val urlGetUpdate = "https://api.telegram.org/bot$token/getUpdates?offset=$updateId"

    val builder = HttpClient.newBuilder()
    val client : HttpClient = builder.build()

    val request : HttpRequest = HttpRequest.newBuilder().uri(URI.create(urlGetUpdate)).build()

    val response : HttpResponse<String> = client.send(request, HttpResponse.BodyHandlers.ofString())

    return response.body()
}