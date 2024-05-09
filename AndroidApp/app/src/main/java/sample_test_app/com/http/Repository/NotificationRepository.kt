package sample_test_app.com.http.Repository

import Notification
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.content.TextContent
import io.ktor.http.contentType
import io.ktor.util.InternalAPI
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

@Serializable
data class NotificationsResponse(
    val allNotif: List<Notification>
)

class NotificationRepository(private val httpClient: HttpClient) {
    suspend fun getNotifications(jwtToken: String): List<Notification> {
        try {
            val response: HttpResponse = withContext(Dispatchers.IO) {
                httpClient.get("https://3proj-back.tristan-tourbier.com/api/notifs") {
                    contentType(ContentType.Application.Json)
                    header("Authorization", "Bearer $jwtToken")
                }
            }
            return if (response.status == HttpStatusCode.OK) {
                val responseBody = response.body<String>()
                val notificationsResponse = Json.decodeFromString<NotificationsResponse>(responseBody)
                notificationsResponse.allNotif
            } else {
                println("Error: ${response.status}")
                emptyList()
            }
        } catch (e: Exception) {
            println("Error: $e")
            return emptyList()
        }
    }

    @OptIn(InternalAPI::class)
    suspend fun deleteNotification(jwtToken: String, notificationId: String): Boolean {
        try {
            println("{\"id_notif\":\"$notificationId\"}",)
            val response: HttpResponse = withContext(Dispatchers.IO) {
                httpClient.delete("https://3proj-back.tristan-tourbier.com/api/notifs") {
                    contentType(ContentType.Application.Json)
                    header("Authorization", "Bearer $jwtToken")
                    body = TextContent(
                        text = "{\n" +
                                "    \"id_notif\":\"$notificationId\"\n" +
                                "}",
                        contentType = ContentType.Application.Json
                    )
                }
            }
            return response.status == HttpStatusCode.OK
        } catch (e: Exception) {
            println("Error: $e")
            return false
        }
    }
}