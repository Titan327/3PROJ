package sample_test_app.com.http.Repository

import io.ktor.client.HttpClient
import io.ktor.client.request.header
import io.ktor.client.request.put
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.util.InternalAPI
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.encodeToJsonElement

class ProfilRepository(private val httpClient: HttpClient) {

    @OptIn(InternalAPI::class)
    suspend fun updateUser(userId: String, token: String, firstname: String, lastname: String, username: String, email: String, birth_date: String, password: String): HttpResponse {
        val url = "https://3proj-back.tristan-tourbier.com/api/users/$userId"

        val userInfos = mapOf(
            "firstname" to firstname,
            "lastname" to lastname,
            "username" to username,
            "email" to email,
            "birth_date" to birth_date
        )

        val requestBody = mapOf(
            "userInfos" to userInfos,
            "password" to password
        )

        return httpClient.put(url) {
            contentType(ContentType.Application.Json)
            header("Authorization", "Bearer $token")
            body = Json.encodeToJsonElement(requestBody)
        }
    }
}