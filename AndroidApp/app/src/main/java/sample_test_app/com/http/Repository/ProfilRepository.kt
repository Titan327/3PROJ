import androidx.navigation.NavHostController
import io.ktor.client.HttpClient
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.client.statement.*
import io.ktor.http.content.*
import io.ktor.util.InternalAPI
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import sample_test_app.com.models.User

class ProfilRepository(private val httpClient: HttpClient, navController: NavHostController) {

    @OptIn(InternalAPI::class)
    suspend fun updateUser(userId: String, user: User, jwtToken: String): HttpResponse {
        val url = "https://3proj-back.tristan-tourbier.com/api/users/$userId"

        val userInfos = mapOf(
            "firstname" to user.firstname,
            "lastname" to user.lastname,
            "username" to user.username,
            "email" to user.email,
            "birth_date" to user.birth_date
        )

        val requestBody = mapOf(
            "userInfos" to userInfos,
        )

        val response: HttpResponse = httpClient.request(url) {
            method = HttpMethod.Put
            contentType(ContentType.Application.Json)
            header(HttpHeaders.Authorization, "Bearer $jwtToken")
            body = TextContent(Json.encodeToString(requestBody), ContentType.Application.Json)
        }

        return response
    }
}

