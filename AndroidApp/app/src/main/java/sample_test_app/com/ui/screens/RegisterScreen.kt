package sample_test_app.com.ui.screens

import android.util.Patterns
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
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
import sample_test_app.com.http.Security.JwtUtils.JwtUtils
import sample_test_app.com.models.User
import java.text.SimpleDateFormat

@Serializable
data class UserInfos(
    val firstname: String,
    val lastname: String,
    val username: String,
    val email: String,
    val birth_date: String,
    val password: String
)

@OptIn(InternalAPI::class)
@Composable
fun RegisterScreen(
    navController: NavHostController,
    httpClient: HttpClient,
    jwtToken: MutableState<String>,
    user: MutableState<User>
) {
    val firstnameState = remember { mutableStateOf("") }
    val lastnameState = remember { mutableStateOf("") }
    val usernameState = remember { mutableStateOf("") }
    val emailState = remember { mutableStateOf("") }
    val passwordState = remember { mutableStateOf("") }
    val passwordConfirmationState = remember { mutableStateOf("") }
    val dateOfBirthState = remember { mutableStateOf("") }
    val jwtToken = remember { mutableStateOf("") }
    val user = remember { mutableStateOf(User()) }

    val emailErrorState = remember { mutableStateOf(false) }
    val passwordErrorState = remember { mutableStateOf(false) }

    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .verticalScroll(scrollState),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = sample_test_app.com.R.drawable.logobig),
            contentDescription = null
        )
        Text(text = "Register")

        TextField(
            value = firstnameState.value,
            onValueChange = { firstnameState.value = it },
            placeholder = { Text("Enter your firstname") },
            colors = TextFieldDefaults.textFieldColors(backgroundColor = Color.White),
            modifier = Modifier.padding(vertical = 8.dp)
        )

        TextField(
            value = lastnameState.value,
            onValueChange = { lastnameState.value = it },
            placeholder = { Text("Enter your lastname") },
            colors = TextFieldDefaults.textFieldColors(backgroundColor = Color.White),
            modifier = Modifier.padding(vertical = 8.dp)
        )

        TextField(
            value = usernameState.value,
            onValueChange = { usernameState.value = it },
            placeholder = { Text("Enter your username") },
            colors = TextFieldDefaults.textFieldColors(backgroundColor = Color.White),
            modifier = Modifier.padding(vertical = 8.dp)
        )

        TextField(
            value = emailState.value,
            onValueChange = { newValue ->
                emailState.value = newValue
                emailErrorState.value = !Patterns.EMAIL_ADDRESS.matcher(newValue).matches()
            },
            placeholder = { Text("Enter your email") },
            colors = TextFieldDefaults.textFieldColors(backgroundColor = Color.White),
            isError = emailErrorState.value,
            modifier = Modifier.padding(vertical = 8.dp)
        )

        TextField(
            value = passwordState.value,
            onValueChange = { passwordState.value = it },
            placeholder = { Text("Enter your password") },
            visualTransformation = PasswordVisualTransformation(),
            colors = TextFieldDefaults.textFieldColors(backgroundColor = Color.White),
            modifier = Modifier.padding(vertical = 8.dp)
        )

        TextField(
            value = passwordConfirmationState.value,
            onValueChange = { newValue ->
                passwordConfirmationState.value = newValue
                passwordErrorState.value = passwordState.value != passwordConfirmationState.value
            },
            placeholder = { Text("Confirm your password") },
            visualTransformation = PasswordVisualTransformation(),
            colors = TextFieldDefaults.textFieldColors(backgroundColor = Color.White),
            isError = passwordErrorState.value,
            modifier = Modifier.padding(vertical = 8.dp)
        )

        TextField(
            value = dateOfBirthState.value,
            onValueChange = { dateOfBirthState.value = it },
            placeholder = { Text("Enter your date of birth") },
            colors = TextFieldDefaults.textFieldColors(backgroundColor = Color.White),
            modifier = Modifier.padding(vertical = 8.dp)
        )

        Button(onClick = {
            val userInfo = UserInfos(
                firstname = firstnameState.value.trim(),
                lastname = lastnameState.value.trim(),
                username = usernameState.value.trim(),
                email = emailState.value.trim(),
                birth_date = dateOfBirthState.value.trim(),
                password = passwordState.value.trim()
            )
            val userInfoJson = Json.encodeToString(mapOf("userInfos" to userInfo))

            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val response: HttpResponse =
                        httpClient.post("https://3proj-back.tristan-tourbier.com/api/auth/register") {
                            contentType(ContentType.Application.Json)
                            body = userInfoJson
                        }
                    if (response.status == HttpStatusCode.OK || response.status == HttpStatusCode.Created) {
                        withContext(Dispatchers.Main) {
                            println("Registration succeeded.")
                            navController.navigate("login/${usernameState.value}/${passwordState.value}")
                        }
                    } else {
                        withContext(Dispatchers.Main) {
                            println("Registration failed. Error code: ${response.status.value}")
                        }
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        println("An error occurred while sending the request: ${e.message}")
                    }
                }
            }
        }) {
            Text("Register")
        }
    }
}
