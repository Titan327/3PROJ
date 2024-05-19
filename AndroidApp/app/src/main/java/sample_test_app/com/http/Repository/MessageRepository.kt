package sample_test_app.com.http.Repository

import android.util.Log
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.util.InternalAPI
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonObject
import sample_test_app.com.models.Group
import sample_test_app.com.models.Message
import sample_test_app.com.models.MessagesContainer

class MessageRepository (private val httpClient: HttpClient) {

    @OptIn(InternalAPI::class)
    suspend fun sendMessage(jwtToken: String, groupId: String, text: String): Int {
        return try {
            val response: HttpResponse = withContext(Dispatchers.IO) {
                httpClient.post("https://3proj-back.tristan-tourbier.com/api/messages/$groupId") {
                    contentType(ContentType.Application.Json)
                    header("Authorization", "Bearer $jwtToken")
                    body = """
                {
                    "text": "$text"
                }
                """.trimIndent()
                }
            }
            response.status.value
        } catch (e: Exception) {
            println("Error: $e")
            -1
        }
    }

    suspend fun getmessage(groupId: String,limit: String,page: String, jwtToken: String): List<Message>? {
        try {
            val messageResponse: HttpResponse = withContext(Dispatchers.IO) {
                httpClient.get("https://3proj-back.tristan-tourbier.com/api/messages/${groupId}?limit=${limit}&page=${page}") {
                    contentType(ContentType.Application.Json)
                    header("Authorization", "Bearer $jwtToken")
                }
            }
            return if (messageResponse.status == HttpStatusCode.OK) {
                val responseBody = messageResponse.body<String>()
                val messagesContainer = Json.decodeFromString<MessagesContainer>(responseBody)
                val messages = messagesContainer.messages

                messages

            } else {
                println("requests: ${messageResponse.status}")
                null
            }
        } catch (e: Exception) {
            println("requests: $e")
            return null
        }
    }


    @OptIn(InternalAPI::class)
    suspend fun sendMessagePrivate(jwtToken: String, groupId: String, text: String): Int {
        return try {
            val response: HttpResponse = withContext(Dispatchers.IO) {
                httpClient.post("https://3proj-back.tristan-tourbier.com/api/messages/private/$groupId") {
                    contentType(ContentType.Application.Json)
                    header("Authorization", "Bearer $jwtToken")
                    body = """
                {
                    "text": "$text"
                }
                """.trimIndent()
                }
            }
            response.status.value
        } catch (e: Exception) {
            println("Error: $e")
            -1
        }
    }

    suspend fun getmessagePrivate(groupId: String,limit: String,page: String, jwtToken: String): List<Message>? {
        try {
            val messageResponse: HttpResponse = withContext(Dispatchers.IO) {
                httpClient.get("https://3proj-back.tristan-tourbier.com/api/messages/private/${groupId}?limit=${limit}&page=${page}") {
                    contentType(ContentType.Application.Json)
                    header("Authorization", "Bearer $jwtToken")
                }
            }
            return if (messageResponse.status == HttpStatusCode.OK) {
                val responseBody = messageResponse.body<String>()
                val messagesContainer = Json.decodeFromString<MessagesContainer>(responseBody)
                val messages = messagesContainer.messages

                messages

            } else {
                println("requests: ${messageResponse.status}")
                null
            }
        } catch (e: Exception) {
            println("requests: $e")
            return null
        }
    }

}