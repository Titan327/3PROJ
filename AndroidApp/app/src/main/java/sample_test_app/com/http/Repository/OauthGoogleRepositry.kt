package sample_test_app.com.http.Repository

import android.util.Log
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.util.InternalAPI
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import sample_test_app.com.models.Category

class OauthGoogleRepositry (private val httpClient: HttpClient) {
    @OptIn(InternalAPI::class)
    suspend fun PostTokenGoogle(jwtGoogleToken: String): HttpResponse? {
        return try {
            val response: HttpResponse = withContext(Dispatchers.IO) {
                httpClient.post("https://3proj-back.tristan-tourbier.com/api/oauth2/google") {
                    contentType(ContentType.Application.Json)
                    body = """
                {
                    "token": "$jwtGoogleToken"
                }
                """.trimIndent()
                }
            }

            response


        } catch (e: Exception) {
            println("Error: $e")
            null
        }
    }
}