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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import sample_test_app.com.models.Refund

class RefundRepository(private val httpClient: HttpClient) {
    suspend fun getGroupRefunds(jwtToken: String, groupId: String): List<Refund> {
        try {
            val response: HttpResponse = withContext(Dispatchers.IO) {
                httpClient.get("https://3proj-back.tristan-tourbier.com/api/groups/$groupId/refunds") {
                    contentType(ContentType.Application.Json)
                    header("Authorization", "Bearer $jwtToken")
                }
            }
            return if (response.status == HttpStatusCode.OK) {
                val responseBody = response.body<String>()
                Json.decodeFromString<List<Refund>>(responseBody)
            } else {
                println("Error: ${response.status}")
                emptyList()
            }
        } catch (e: Exception) {
            println("Error: $e")
            return emptyList()
        }
    }

    suspend fun getGroupDoneRefunds(jwtToken: String, groupId: String): List<Refund> {
        try {
            val response: HttpResponse = withContext(Dispatchers.IO) {
                httpClient.get("https://3proj-back.tristan-tourbier.com/api/groups/$groupId/refunds/done") {
                    contentType(ContentType.Application.Json)
                    header("Authorization", "Bearer $jwtToken")
                }
            }
            return if (response.status == HttpStatusCode.OK) {
                val responseBody = response.body<String>()
                Json.decodeFromString<List<Refund>>(responseBody)
            } else {
                println("Error: ${response.status}")
                emptyList()
            }
        } catch (e: Exception) {
            println("Error: $e")
            return emptyList()
        }
    }

    suspend fun makeARefund(jwtToken: String, groupId: String, refundId: String): Boolean {
        return try {
            val response: HttpResponse = withContext(Dispatchers.IO) {
                httpClient.post("https://3proj-back.tristan-tourbier.com/api/groups/$groupId/refund/$refundId") {
                    contentType(ContentType.Application.Json)
                    header("Authorization", "Bearer $jwtToken")
                }
            }
            response.status == HttpStatusCode.OK
        } catch (e: Exception) {
            println("Error: $e")
            false
        }
    }
}


