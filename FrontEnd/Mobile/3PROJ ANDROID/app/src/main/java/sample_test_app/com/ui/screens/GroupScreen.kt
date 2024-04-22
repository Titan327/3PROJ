package sample_test_app.com.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive



@Composable
fun GroupScreen(groupId: String, httpClient: HttpClient, jwtToken: String) {
    val groupInfoState = remember { mutableStateOf("") }
    val errorState = remember { mutableStateOf("") }

    LaunchedEffect(key1 = groupId) {
        CoroutineScope(Dispatchers.Main).launch {
            val groupInfoResponse: HttpResponse = withContext(Dispatchers.IO) {
                httpClient.get("https://3proj-back.tristan-tourbier.com/api/groups/$groupId") {
                    contentType(ContentType.Application.Json)
                    header("Authorization", "Bearer $jwtToken")
                }
            }
            if (groupInfoResponse.status == HttpStatusCode.OK) {
                val groupInfoResponseBody = groupInfoResponse.bodyAsText()
                withContext(Dispatchers.Main) {
                    println("Group info request succeeded. Response: $groupInfoResponseBody")

                    val groupInfoJson = Json.parseToJsonElement(groupInfoResponseBody).jsonObject
                    val groupName = groupInfoJson["name"]?.jsonPrimitive?.content

                    if (groupName != null) {
                        groupInfoState.value = groupName
                    }
                }
            } else {
                withContext(Dispatchers.Main) {
                    println("Group info request failed. Error code: ${groupInfoResponse.status.value}")
                    errorState.value = "Failed to load group info. Please try again later."
                }
            }
        }
    }

    Column {
        Text(text = "Group Info:")
        if (errorState.value.isNotEmpty()) {
            Text(text = errorState.value, color = Color.Red)
        } else {
            Text(text = groupInfoState.value)
        }
    }
}