package sample_test_app.com.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Button
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.rememberImagePainter
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
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController

import coil.transform.CircleCropTransformation





@Composable
fun HomeScreen(userId: String, httpClient: HttpClient, jwtToken: String, navController: NavController) {
    var usernameState = remember { mutableStateOf("") }
    var profilePictureState = remember { mutableStateOf("") }
    var groupsState = remember { mutableStateOf(listOf<String>()) }
    var transactionsState = remember { mutableStateOf(listOf<Map<String, String>>()) } // New state for transactions

    LaunchedEffect(key1 = userId) {
        CoroutineScope(Dispatchers.Main).launch {
            val userInfoResponse: HttpResponse = withContext(Dispatchers.IO) {
                httpClient.get("https://3proj-back.tristan-tourbier.com/api/user/$userId") {
                    contentType(ContentType.Application.Json)
                    header("Authorization", "Bearer $jwtToken")
                }
            }
            if (userInfoResponse.status == HttpStatusCode.OK) {
                val userInfoResponseBody = userInfoResponse.bodyAsText()
                withContext(Dispatchers.Main) {
                    println("User info request succeeded. Response: $userInfoResponseBody")

                    val userInfoJson = Json.parseToJsonElement(userInfoResponseBody).jsonObject
                    val username = userInfoJson["username"]?.jsonPrimitive?.content
                    val profilePicture = userInfoJson["profile_picture"]?.jsonArray?.get(0)?.jsonPrimitive?.content

                    if (username != null) {
                        usernameState.value = username
                    }
                    if (profilePicture != null) {
                        profilePictureState.value = profilePicture
                    }
                }
            } else {
                withContext(Dispatchers.Main) {
                    println("User info request failed. Error code: ${userInfoResponse.status.value}")
                }
            }

            val groupsResponse: HttpResponse = withContext(Dispatchers.IO) {
                httpClient.get("https://3proj-back.tristan-tourbier.com/api/user/$userId/groups") {
                    contentType(ContentType.Application.Json)
                    header("Authorization", "Bearer $jwtToken")
                }
            }
            if (groupsResponse.status == HttpStatusCode.OK) {
                val groupsResponseBody = groupsResponse.bodyAsText()
                withContext(Dispatchers.Main) {
                    println("Groups request succeeded. Response: $groupsResponseBody")

                    val groupsJson = Json.parseToJsonElement(groupsResponseBody).jsonArray
                    val groups = groupsJson.mapNotNull { it.jsonObject["name"]?.jsonPrimitive?.content }
                    groupsState.value = groups
                }
            } else {
                withContext(Dispatchers.Main) {
                    println("Groups request failed. Error code: ${groupsResponse.status.value}")
                }
            }

            val transactionsResponse: HttpResponse = withContext(Dispatchers.IO) {
                httpClient.get("https://3proj-back.tristan-tourbier.com/api/user/$userId/transactions") {
                    contentType(ContentType.Application.Json)
                    header("Authorization", "Bearer $jwtToken")
                }
            }
            if (transactionsResponse.status == HttpStatusCode.OK) {
                val transactionsResponseBody = transactionsResponse.bodyAsText()
                withContext(Dispatchers.Main) {
                    println("Transactions request succeeded. Response: $transactionsResponseBody")

                    val transactionsJson = Json.parseToJsonElement(transactionsResponseBody).jsonArray
                    val transactions = transactionsJson.mapNotNull { it.jsonObject }
                        .mapNotNull {
                            val amount = it["amount"]?.jsonPrimitive?.content
                            val description = it["description"]?.jsonPrimitive?.content
                            val date = it["date"]?.jsonPrimitive?.content
                            if (amount != null && description != null && date != null) {
                                mapOf(
                                    "amount" to amount,
                                    "description" to description,
                                    "date" to date
                                )
                            } else {
                                null
                            }
                        }
                    transactionsState.value = transactions
                }
            } else {
                withContext(Dispatchers.Main) {
                    println("Transactions request failed. Error code: ${transactionsResponse.status.value}")
                }
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        Image(
            painter = painterResource(id = R.drawable.logo250),
            contentDescription = "Logo",
            modifier = Modifier.align(Alignment.TopCenter)
        )

        if (profilePictureState.value.isNotBlank() && profilePictureState.value != "null") {
            Image(
                painter = rememberImagePainter(
                    data = profilePictureState.value,
                    builder = {
                        transformations(CircleCropTransformation())
                    }
                ),
                contentDescription = "User Profile Picture",
                modifier = Modifier
                    .size(80.dp)
                    .align(Alignment.TopEnd)
                    .padding(top = 16.dp)
                    .clickable {
                        navController.navigate("ProfilScreen/$userId/$jwtToken")
                    }
            )
        } else {
            Image(
                painter = rememberImagePainter(
                    data = R.drawable.userdefault,
                    builder = {
                        transformations(CircleCropTransformation())
                    }
                ),
                contentDescription = "User Profile Picture",
                modifier = Modifier
                    .size(80.dp)
                    .align(Alignment.TopEnd)
                    .padding(top = 16.dp)
                    .clickable {
                        navController.navigate("ProfilScreen/$userId/$jwtToken")
                    }
            )
        }

        LazyColumn(
            modifier = Modifier.align(Alignment.Center)
        ) {
            items(groupsState.value) { group ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    elevation = 4.dp
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.grouplogo),
                            contentDescription = "Group Logo",
                            modifier = Modifier
                                .size(50.dp)
                                .padding(end = 16.dp)
                        )
                        Text(text = group)
                    }
                }
            }

            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp, horizontal = 16.dp), // Ajout de padding horizontal
                    elevation = 4.dp
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp) // Ajout de padding à l'intérieur de la Card
                    ) {
                        Text(text = "Dernières dépenses", style = MaterialTheme.typography.h6)

                        // En-têtes du tableau
                        Row(
                            modifier = Modifier
                                .background(Color.LightGray)
                                .padding(vertical = 8.dp)
                        ) {
                            Text(text = "Nom", modifier = Modifier.weight(1f), style = MaterialTheme.typography.subtitle1)
                            Text(text = "Date", modifier = Modifier.weight(1f), style = MaterialTheme.typography.subtitle1)
                            Text(text = "Somme", modifier = Modifier.weight(1f), style = MaterialTheme.typography.subtitle1)
                            Text(text = "Statut", modifier = Modifier.weight(1f), style = MaterialTheme.typography.subtitle1)
                        }

                        if (transactionsState.value.isEmpty()) {
                            Text(text = "Rien à afficher", style = MaterialTheme.typography.body1)
                        } else {
                            transactionsState.value.forEach { transaction ->
                                // Ligne du tableau pour chaque transaction
                                Row(
                                    modifier = Modifier
                                        .border(1.dp, Color.LightGray)
                                        .padding(vertical = 8.dp)
                                ) {
                                    Text(text = transaction["description"] ?: "", modifier = Modifier.weight(1f), style = MaterialTheme.typography.body1)
                                    Text(text = transaction["date"] ?: "", modifier = Modifier.weight(1f), style = MaterialTheme.typography.body1)
                                    Text(text = transaction["amount"] ?: "", modifier = Modifier.weight(1f), style = MaterialTheme.typography.body1)
                                    Text(text = "Paid", modifier = Modifier.weight(1f), style = MaterialTheme.typography.body1) // Remplacez "Paid" par le statut réel
                                }
                            }
                        }
                    }
                }
            }
        }




        Image(
            painter = painterResource(id = R.drawable.notificationbellhome),
            contentDescription = "Notification Icon",
            modifier = Modifier
                .size(40.dp)
                .align(Alignment.BottomStart)
                .offset(x = 26.dp)
                .offset(y = -20.dp)
                .clickable { /* Ajoutez votre action de clic ici */ }
        )

        Image(
            painter = painterResource(id = R.drawable.homepage),
            contentDescription = "Home Icon",
            modifier = Modifier
                .size(40.dp)
                .align(Alignment.BottomCenter)
                .offset(y = -20.dp)
                .clickable { /* Ajoutez votre action de clic ici */ }
        )

        Image(
            painter = painterResource(id = R.drawable.messsagecircular),
            contentDescription = "Settings Icon",
            modifier = Modifier
                .size(30.dp)
                .align(Alignment.BottomEnd)
                .offset(x = -26.dp)
                .offset(y = -20.dp)
                .clickable { /* Ajoutez votre action de clic ici */ }
        )
    }
}