import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

enum class TypeOfRegex(val regex: Regex) {
    UPDATE_ID("\"update_id\":([0-9]+)".toRegex()),
    CHAT_ID("\"id\":([0-9]+)".toRegex()),
    MESSAGE("\"text\":\"(.+?)\"".toRegex()),
}

private fun fromJsonToValue(
    typeOfRegex: TypeOfRegex,
    updates: String,
) : String? {
    val matchResult: MatchResult? = typeOfRegex.regex.find(updates)
    val groupsUpdate = matchResult?.groups
    return groupsUpdate?.get(1)?.value
}

fun main(args: Array<String>) {
    val telegramBotService = TelegramBotService()
    val token = args[0]
    var updateId = 0

    while (true) {
        Thread.sleep(2000)
        val updates: String = telegramBotService.getUpdates(token, updateId)
        println(updates)

        val updateIdString = fromJsonToValue(TypeOfRegex.UPDATE_ID, updates) ?: continue
        updateId = updateIdString.toInt() + 1

        val chatId = fromJsonToValue(TypeOfRegex.CHAT_ID, updates)
        val message = fromJsonToValue(TypeOfRegex.MESSAGE, updates)?.replace(" ", "%20")
        telegramBotService.sendMessage(token, chatId, message)
    }
}