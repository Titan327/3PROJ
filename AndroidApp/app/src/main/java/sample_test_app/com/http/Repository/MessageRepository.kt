package sample_test_app.com.http.Repository

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
import sample_test_app.com.models.Group

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

    suspend fun getmessage(groupId: String, jwtToken: String): Group {
        try {
            val groupResponse: HttpResponse = withContext(Dispatchers.IO) {
                httpClient.get("https://3proj-back.tristan-tourbier.com/api/groups/${groupId}") {
                    contentType(ContentType.Application.Json)
                    header("Authorization", "Bearer $jwtToken")
                }
            }
            return if (groupResponse.status == HttpStatusCode.OK) {
                val responseBody = groupResponse.body<String>()
                Json.decodeFromString<Group>(responseBody)
            } else {
                println("Error: ${groupResponse.status}")
                Group()
            }
        } catch (e: Exception) {
            println("Error: $e")
            return Group()
        }
    }

}