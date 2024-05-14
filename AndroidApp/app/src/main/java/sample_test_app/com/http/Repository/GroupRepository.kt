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

class GroupRepository(private val httpClient: HttpClient) {
    suspend fun getUserGroups(userId: String, jwtToken: String, favorite: Boolean): List<Group> {
        try {
            val userResponse: HttpResponse = if (favorite) {
                withContext(Dispatchers.IO) {
                    httpClient.get("https://3proj-back.tristan-tourbier.com/api/users/$userId/groups?favorite=true") {
                        contentType(ContentType.Application.Json)
                        header("Authorization", "Bearer $jwtToken")
                    }
                }
            } else {
                withContext(Dispatchers.IO) {
                    httpClient.get("https://3proj-back.tristan-tourbier.com/api/users/$userId/groups") {
                        contentType(ContentType.Application.Json)
                        header("Authorization", "Bearer $jwtToken")
                    }
                }
            }
            return if (userResponse.status == HttpStatusCode.OK) {
                val responseBody = userResponse.body<String>()
                Json.decodeFromString<List<Group>>(responseBody)
            } else {
                println("Error récupération groupes: ${userResponse.status}")
                emptyList()
            }
        } catch (e: Exception) {
            println("Error: $e")
            return emptyList()
        }
    }

    @OptIn(InternalAPI::class)
    suspend fun createGroup(jwtToken: String, groupName: String, groupDescription: String) {
        try {
            withContext(Dispatchers.IO) {
                httpClient.post("https://3proj-back.tristan-tourbier.com/api/groups") {
                    contentType(ContentType.Application.Json)
                    header("Authorization", "Bearer $jwtToken")
                    body = """
                {
                    "name": "$groupName",
                    "description": "$groupDescription"
                }
                """.trimIndent()
                }
            }
        } catch (e: Exception) {
            println("Error: $e")
        }
    }

    suspend fun getGroup(groupId: String, jwtToken: String): Group {
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


