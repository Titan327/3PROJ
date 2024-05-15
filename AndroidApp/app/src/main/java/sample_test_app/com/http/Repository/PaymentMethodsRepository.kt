package sample_test_app.com.http.Repository

import PaymentMethod
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

class PaymentMethodsRepository(private val httpClient: HttpClient) {
    suspend fun getPaymentMethods(userId: String, groupId: String, jwtToken: String): List<PaymentMethod> {
        try {
            val response: HttpResponse =
                withContext(Dispatchers.IO) {
                    httpClient.get("https://3proj-back.tristan-tourbier.com/api/users/$userId/$groupId/paymentMethode") {
                        contentType(ContentType.Application.Json)
                        header("Authorization", "Bearer $jwtToken")
                    }
                }

            return if (response.status == HttpStatusCode.OK) {
                val responseBody = response.body<String>()
                Json.decodeFromString<List<PaymentMethod>>(responseBody)
            } else {
                println("Error récupération groupes: ${response.status}")
                emptyList()
            }
        } catch (e: Exception) {
            println("Error: $e")
            return emptyList()
        }
    }
}