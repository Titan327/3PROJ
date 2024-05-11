package sample_test_app.com.http.Repository

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import sample_test_app.com.models.Category

class CategoryRepository (private val httpClient: HttpClient) {
    suspend fun getCategories(): List<Category> {
        try {
            val groupResponse: HttpResponse = withContext(Dispatchers.IO) {
                httpClient.get("https://3proj-back.tristan-tourbier.com/api/transactionCategories/") {
                    contentType(ContentType.Application.Json)
                }
            }
            return if (groupResponse.status == HttpStatusCode.OK) {
                val responseBody = groupResponse.body<String>()
                Json.decodeFromString<List<Category>>(responseBody)
            } else {
                println("Error: ${groupResponse.status}")
                return emptyList()
            }
        } catch (e: Exception) {
            println("Error: $e")
            return emptyList()
        }
    }
}