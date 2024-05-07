package sample_test_app.com.http.Repository

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import sample_test_app.com.models.TransactionUser


class TransactionRepository(private val httpClient: HttpClient) {
    suspend fun getLastTransactions(userId: String, jwtToken: String): List<TransactionUser> {
        try {
            val userResponse: HttpResponse = withContext(Dispatchers.IO) {
                httpClient.get("https://3proj-back.tristan-tourbier.com/api/users/${userId}/lastTransactions?limit=3") {
                    contentType(ContentType.Application.Json)
                    header("Authorization", "Bearer $jwtToken")
                }
            }
            return if (userResponse.status == HttpStatusCode.OK) {
                val responseBody = userResponse.body<String>()
                println(responseBody)
                val json = Json { ignoreUnknownKeys = true; isLenient = true }
                return json.decodeFromString<List<TransactionUser>>(responseBody)
            } else {
                println("Error: ${userResponse.status}")
                emptyList()
            }
        } catch (e: Exception) {
            println("Error: $e")
            return emptyList()
        }
    }
}