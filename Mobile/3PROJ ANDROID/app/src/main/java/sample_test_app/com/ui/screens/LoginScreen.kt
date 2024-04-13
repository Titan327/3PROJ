package sample_test_app.com.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.R
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import android.util.Patterns
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.unit.dp
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.client.statement.readText
import io.ktor.client.utils.EmptyContent.contentType
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.util.InternalAPI
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

import sample_test_app.com.http.Security.JwtUtils.JwtUtils


@OptIn(InternalAPI::class)
@Composable
fun LoginScreen(navController: NavHostController, httpClient: HttpClient) {
    // État pour stocker la valeur du champ de texte "Email"
    val emailState = remember { mutableStateOf("") }

    // État pour stocker la valeur du champ de texte "Username"
    val usernameState = remember { mutableStateOf("") }

    // État pour stocker la valeur du champ de texte "Password"
    val passwordState = remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Champ de texte "Email"
        TextField(
            value = emailState.value,
            onValueChange = { newValue ->
                emailState.value = newValue
            },
            placeholder = { Text("Enter your email") },
            colors = TextFieldDefaults.textFieldColors(backgroundColor = Color.White),
            modifier = Modifier.padding(vertical = 8.dp)
        )

        // Champ de texte "Username"
        TextField(
            value = usernameState.value,
            onValueChange = { newValue ->
                usernameState.value = newValue
            },
            placeholder = { Text("Enter your username") },
            colors = TextFieldDefaults.textFieldColors(backgroundColor = Color.White),
            modifier = Modifier.padding(vertical = 8.dp)
        )

        // Champ de texte "Password"
        TextField(
            value = passwordState.value,
            onValueChange = { newValue ->
                passwordState.value = newValue
            },
            placeholder = { Text("Enter your password") },
            visualTransformation = PasswordVisualTransformation(),
            colors = TextFieldDefaults.textFieldColors(backgroundColor = Color.White),
            modifier = Modifier.padding(vertical = 8.dp)
        )

        Button(onClick = {
            val loginInfo = mapOf(
                "email" to emailState.value,
                "username" to usernameState.value,
                "password" to passwordState.value
            )

            val loginInfoJson = Json.encodeToString(loginInfo)
            println(loginInfoJson)

            CoroutineScope(Dispatchers.Main).launch {
                try {
                    val response: HttpResponse = withContext(Dispatchers.IO) {
                        httpClient.post("https://3proj-back.tristan-tourbier.com/api/auth/login") {
                            contentType(ContentType.Application.Json)
                            body = loginInfoJson
                        }
                    }
                    if (response.status == HttpStatusCode.OK) {
                        val responseBody = response.bodyAsText()
                        withContext(Dispatchers.Main) {
                            println("Request succeeded. Response: $responseBody")

                            val jwtPayload = JwtUtils.decodeJWT(responseBody)
                            println("JWT Payload: $jwtPayload")

                            // Print the JWT token
                            println("JWT Token: $responseBody")

                            val jwtPayloadJson = Json.parseToJsonElement(jwtPayload).jsonObject
                            val userId = jwtPayloadJson["userId"]?.jsonPrimitive?.content

                            if (userId != null) {
                                // Navigate to the home screen with the userId
                                navController.navigate("home/$userId/$responseBody")

                                // Get user information
                                CoroutineScope(Dispatchers.Main).launch {
                                    val userInfoResponse: HttpResponse = withContext(Dispatchers.IO) {
                                        httpClient.get("https://3proj-back.tristan-tourbier.com/api/user/$userId") {
                                            contentType(ContentType.Application.Json)
                                        }
                                    }
                                    if (userInfoResponse.status == HttpStatusCode.OK) {
                                        val userInfoResponseBody = userInfoResponse.bodyAsText()
                                        withContext(Dispatchers.Main) {
                                            println("User info request succeeded. Response: $userInfoResponseBody")

                                            val userInfoJson = Json.parseToJsonElement(userInfoResponseBody).jsonObject
                                            val username = userInfoJson["username"]?.jsonPrimitive?.content
                                            val email = userInfoJson["email"]?.jsonPrimitive?.content

                                            println("Username: $username")
                                            println("Email: $email")
                                        }
                                    } else {
                                        withContext(Dispatchers.Main) {
                                            println("User info request failed. Error code: ${userInfoResponse.status.value}")
                                        }
                                    }
                                }
                            } else {
                                println("Error: userId is null")
                            }
                        }
                    } else {
                        withContext(Dispatchers.Main) {
                            println("Request failed. Error code: ${response.status.value}")
                        }
                    }

                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        println("An error occurred while sending the request: ${e.message}")
                    }
                } finally {
                    httpClient.close()
                }
            }
        }) {
            Text("Login")
        }
    }
}