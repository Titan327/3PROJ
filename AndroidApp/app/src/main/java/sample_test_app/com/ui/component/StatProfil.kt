package sample_test_app.com.ui.component

import androidx.compose.foundation.layout.Column
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.sp
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
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import sample_test_app.com.LocalUser

@Serializable
data class UserStatistics(
    val statistic: String,
    val average: String,
    val transactions: Int,
    val amountByCategories: Map<String, Float>,
    val numberByCategories: Map<String, Int>,
    val averageByCategories: Map<String, String>,
    val amountByMonth: Map<String, Float>,
    val numberByMonth: Map<String, Int>
)

suspend fun fetchUserStatistics(httpClient: HttpClient, jwtToken: String, userId: Int): UserStatistics? {
    try {
        val response: HttpResponse = withContext(Dispatchers.IO) {
            httpClient.get("https://3proj-back.tristan-tourbier.com/api/users/$userId/statistics") {
                contentType(ContentType.Application.Json)
                header("Authorization", "Bearer $jwtToken")
            }
        }
        return if (response.status == HttpStatusCode.OK) {
            val responseBody = response.body<String>()
            Json.decodeFromString<UserStatistics>(responseBody)
        } else {
            println("Error: ${response.status}")
            null
        }
    } catch (e: Exception) {
        println("Error: $e")
        return null
    }
}

@Composable
fun UserStatistics(httpClient: HttpClient, jwtToken: String) {
    val user = LocalUser.current
    val userStatistics = remember { mutableStateOf<UserStatistics?>(null) }

    LaunchedEffect(httpClient, jwtToken, user.id) {
        userStatistics.value = user.id?.let { fetchUserStatistics(httpClient, jwtToken, it.toInt()) }
    }

    userStatistics.value?.let {
        Column {
            Text("Statistique du compte sur le mois : ${it.statistic}", color = Color.White, style = TextStyle(fontSize = 20.sp))
            Text("Moyenne: ${it.average}", color = Color.White, style = TextStyle(fontSize = 20.sp))
            Text("Transactions: ${it.transactions}", color = Color.White, style = TextStyle(fontSize = 20.sp))

            val totalAmount = it.amountByCategories.values.sum()

            it.amountByCategories.forEach { (category, amount) ->
                val percentage = amount / totalAmount * 100
                Text("Catégorie : $category, Pourcentage: %.2f%%".format(percentage), color = Color.White, style = TextStyle(fontSize = 20.sp))
            }
        }
    }
}