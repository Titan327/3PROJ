package sample_test_app.com.ui.screens



import GoogleSignInButton
import androidx.compose.foundation.Image
import androidx.compose.foundation.background

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.util.InternalAPI
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import sample_test_app.com.R
import sample_test_app.com.http.Security.JwtUtils.JwtUtils
import sample_test_app.com.models.User

@Serializable
data class TokenResponse(
    val token: String?,
    val oauth: String?
)



@OptIn(InternalAPI::class)
@Composable
fun LoginScreen(
    navController: NavHostController,
    httpClient: HttpClient,
    jwtToken: MutableState<String>,
    user: MutableState<User>,
    username: String,
    password: String
) {
    val usernameState = remember { mutableStateOf(username) }
    val passwordState = remember { mutableStateOf(password) }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.logobig),
            contentDescription = "Logo"
        )
        TextField(
            value = usernameState.value,
            onValueChange = { newValue ->
                usernameState.value = newValue
            },
            placeholder = { Text("Entrer votre pseudo") },
            colors = TextFieldDefaults.textFieldColors(backgroundColor = Color.White),
            modifier = Modifier.padding(vertical = 8.dp)
        )

        TextField(
            value = passwordState.value,
            onValueChange = { newValue ->
                passwordState.value = newValue
            },
            placeholder = { Text("Entrer votre mot de passe") },
            visualTransformation = PasswordVisualTransformation(),
            colors = TextFieldDefaults.textFieldColors(backgroundColor = Color.White),
            modifier = Modifier.padding(vertical = 8.dp)
        )

        Button(onClick = {
            val loginInfo = mapOf(
                "username" to usernameState.value,
                "password" to passwordState.value
            )

            val loginInfoJson = Json.encodeToString(loginInfo)

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


                            val tokenResponse = Json.decodeFromString<TokenResponse>(responseBody)
                            jwtToken.value = tokenResponse.token.toString()

                            val jwtPayload = JwtUtils.decodeJWT(jwtToken.value)
                            val jwtPayloadJson = Json.parseToJsonElement(jwtPayload).jsonObject
                            val userId = jwtPayloadJson["userId"]?.jsonPrimitive?.content

                            if (userId != null) {

                                val userResponse: HttpResponse = withContext(Dispatchers.IO) {
                                    httpClient.get("https://3proj-back.tristan-tourbier.com/api/users/${userId}") {
                                        contentType(ContentType.Application.Json)
                                        header("Authorization", "Bearer ${jwtToken.value}")
                                        println("Received token: ${jwtToken.value}")
                                    }
                                }
                                if (userResponse.status == HttpStatusCode.OK) {
                                    val userResponseBody = userResponse.bodyAsText()
                                    val userToAssign = Json.decodeFromString<User>(userResponseBody)
                                    user.value = userToAssign
                                }

                                navController.navigate("home")
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
                }
            }
        },
            colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFFFFA31A))



        ) {
            Text("Connexion", color = Color.White)


        }

        GoogleSignInButton(navController,httpClient,jwtToken,user)

    }
}