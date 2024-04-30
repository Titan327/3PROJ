package sample_test_app.com.http.repositories

import io.ktor.client.call.body
import io.ktor.client.request.post

import io.ktor.client.request.*
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import sample_test_app.com.http.AppHttpClient
import sample_test_app.com.http.types.UserInfos


class AuthRepository {
    companion object {
        private val client = AppHttpClient.getClient()
        private const val BASE_URL = "{{base-URL}}"
        private const val REGISTER_ENDPOINT = "/api/auth/register"
    }

    suspend fun registerUser(userInfo: UserInfos) {
        val response: HttpResponse = client.post("$BASE_URL$REGISTER_ENDPOINT") {
            contentType(ContentType.Application.Json)
            setBody(Json.encodeToString(userInfo))
        }
    
        // Vérifier la réponse ici si nécessaire
        if (!response.status.isSuccess()) {
            val errorMessage = response.body<String>()
            // Gérer l'échec de l'inscription
            println("Erreur lors de l'inscription: $errorMessage")
        } else {
            // Gérer le succès de l'inscription
            println("Inscription réussie!")
        }
    }
}

