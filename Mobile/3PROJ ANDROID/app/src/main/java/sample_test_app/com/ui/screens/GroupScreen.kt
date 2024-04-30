package sample_test_app.com.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavBackStackEntry
import coil.compose.rememberImagePainter
import coil.transform.CircleCropTransformation
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
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import sample_test_app.com.R

@Composable
fun GroupScreen(backStackEntry: NavBackStackEntry, httpClient: HttpClient) {
    val arguments = backStackEntry.arguments
    val groupId = arguments?.getString("groupId") ?: ""
    val jwtToken = arguments?.getString("jwtToken") ?: ""
    var lastTransactionsState = remember { mutableStateOf(listOf<Map<String, String>>()) } // New state for last transactions


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
                    groupInfoState.value = groupInfoResponseBody
                }
            } else {
                withContext(Dispatchers.Main) {
                    errorState.value = "Failed to fetch group info. Error code: ${groupInfoResponse.status.value}"
                }
            }
        }
    }

    val groupInfoJson = if (groupInfoState.value.isNotBlank()) {
        Json.parseToJsonElement(groupInfoState.value).jsonObject
    } else {
        null
    }

    val groupName = groupInfoJson?.get("name")?.jsonPrimitive?.content ?: ""
    val groupDescription = groupInfoJson?.get("description")?.jsonPrimitive?.content ?: ""

    val groupPictures = groupInfoJson?.get("picture")?.jsonArray
    val groupPicture100 = groupPictures?.find { it.jsonPrimitive.content.contains("/100") }?.jsonPrimitive?.content

    val groupUsers = groupInfoJson?.get("Users")?.jsonArray
    val groupMembersCount = groupUsers?.size ?: 0

    Column {
        Row(
            modifier = Modifier.padding(start = 8.dp, top = 8.dp)
        ) {
            if (groupPicture100 != null) {
                Image(
                    painter = rememberImagePainter(
                        data = groupPicture100,
                        builder = {
                            transformations(CircleCropTransformation())
                        }
                    ),
                    contentDescription = "Group Picture",
                    modifier = Modifier.size(100.dp)
                )
            } else {
                Image(
                    painter = painterResource(id = R.drawable.grouplogo),
                    contentDescription = "Default Group Picture",
                    modifier = Modifier.size(100.dp)
                )
            }

            Column(
                modifier = Modifier.padding(start = 8.dp)
            ) {
                Text(text = groupName, style = TextStyle(fontSize = 50.sp, fontWeight = FontWeight.Bold, color = Color.White))
                Text(text = groupDescription, color = Color.White, fontSize = 30.sp)
                Text(text = "Nombre de membres : $groupMembersCount", style = TextStyle(fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.White))
            }
        }

        Row(
            modifier = Modifier.horizontalScroll(rememberScrollState())
        ) {
            groupUsers?.forEach { userJsonElement ->
                val userJsonObject = userJsonElement.jsonObject
                val userProfilePictures = userJsonObject["profile_picture"]?.jsonArray
                val userProfilePicture100 = userProfilePictures?.find { it.jsonPrimitive.content.contains("/100") }?.jsonPrimitive?.content

                if (userProfilePicture100 != null) {
                    Image(
                        painter = rememberImagePainter(
                            data = userProfilePicture100,
                            builder = {
                                transformations(CircleCropTransformation())
                            }
                        ),
                        contentDescription = "User Profile Picture",
                        modifier = Modifier.size(50.dp)
                    )
                } else {
                    Image(
                        painter = painterResource(id = R.drawable.userdefault),
                        contentDescription = "Default User Picture",
                        modifier = Modifier.size(50.dp)
                    )
                }
            }
        }

        if (errorState.value.isNotEmpty()) {
            Text(text = errorState.value, color = Color.Red)
        }
    }
}