package sample_test_app.com.ui.screens

import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import io.ktor.client.*
import io.ktor.client.request.*
import kotlinx.serialization.encodeToString
import io.ktor.http.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import androidx.compose.material.Button
import io.ktor.client.statement.HttpResponse
import io.ktor.util.InternalAPI
import kotlinx.serialization.Serializable
import sample_test_app.com.LocalUser





@Serializable
data class PasswordInfo(
    val password: String,
    val userInfos: UserPasswordInfos
)
@Serializable
data class UserPasswordInfos(
    val password: String,
    val passwordConfirm: String
)


@OptIn(InternalAPI::class)
@Composable
fun PasswordChangeSection(currentPasswordState: MutableState<String>, newPasswordState: MutableState<String>, repeatNewPasswordState: MutableState<String>, httpClient: HttpClient, jwtToken: String) {
    val user = LocalUser.current

    TextField(
        value = currentPasswordState.value,
        onValueChange = { currentPasswordState.value = it },
        label = { Text("Current Password") },
        modifier = Modifier.padding(bottom = 16.dp),
        colors = TextFieldDefaults.textFieldColors(
            textColor = Color.Black,
            cursorColor = Color.Black,
            backgroundColor = Color.White,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent
        )
    )

    TextField(
        value = newPasswordState.value,
        onValueChange = { newPasswordState.value = it },
        label = { Text("New Password") },
        modifier = Modifier.padding(bottom = 16.dp),
        colors = TextFieldDefaults.textFieldColors(
            textColor = Color.Black,
            cursorColor = Color.Black,
            backgroundColor = Color.White,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent
        )
    )

    TextField(
        value = repeatNewPasswordState.value,
        onValueChange = { repeatNewPasswordState.value = it },
        label = { Text("Repeat New Password") },
        modifier = Modifier.padding(bottom = 16.dp),
        colors = TextFieldDefaults.textFieldColors(
            textColor = Color.Black,
            cursorColor = Color.Black,
            backgroundColor = Color.White,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent
        )
    )

    Button(onClick = {
        if (newPasswordState.value == repeatNewPasswordState.value) {
            val passwordInfo = PasswordInfo(
                password = currentPasswordState.value,
                userInfos = UserPasswordInfos(
                    password = newPasswordState.value,
                    passwordConfirm = repeatNewPasswordState.value
                )
            )

            val passwordInfoJson = Json.encodeToString(passwordInfo)

            CoroutineScope(Dispatchers.Main).launch {
                try {
                    val response: HttpResponse = withContext(Dispatchers.IO) {
                        httpClient.put("https://3proj-back.tristan-tourbier.com/api/users/${user.id}/password") { // Use user.id in the URL
                            contentType(ContentType.Application.Json)
                            header("Authorization", "Bearer $jwtToken")
                            body = passwordInfoJson
                        }
                    }
                    if (response.status == HttpStatusCode.OK) {
                        withContext(Dispatchers.Main) {
                        }
                    } else {
                        withContext(Dispatchers.Main) {
                            println("Password update request failed. Error code: ${response.status.value}")
                        }
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        println("An error occurred while sending the request: ${e.message}")
                    }
                }
            }
        } else {
            println("New password and confirmation do not match.")
        }
    }) {
        Text("Change Password")
    }
}