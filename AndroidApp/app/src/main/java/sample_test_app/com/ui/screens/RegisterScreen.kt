package sample_test_app.com.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
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
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.ui.text.input.PasswordVisualTransformation
import io.ktor.client.*
import androidx.compose.material.Button
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import io.ktor.client.request.*
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.*
import io.ktor.util.InternalAPI
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
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
fun RegisterScreen(navController: NavHostController, httpClient: HttpClient) {

    val firstnameState = remember { mutableStateOf("") }
    val lastnameState = remember { mutableStateOf("") }
    val usernameState = remember { mutableStateOf("") }
    val emailState = remember { mutableStateOf("") }
    val isDateOfBirthValid = remember { mutableStateOf(false) }

    val passwordState = remember { mutableStateOf("") }
    val passwordConfirmationState = remember { mutableStateOf("") }
    val isFirstnameSubmitted = remember { mutableStateOf(false) }
    val isLastnameSubmitted = remember { mutableStateOf(false) }
    val isUsernameSubmitted = remember { mutableStateOf(false) }
    val isEmailSubmitted = remember { mutableStateOf(false) }
    val emailErrorState = remember { mutableStateOf(false) }
    val showDateOfBirthField = remember { mutableStateOf(false) }
    val dateOfBirthState = remember { mutableStateOf("") }


    @Serializable
    data class UserInfosWrapper(val userInfos: UserInfos)
    val isPasswordSubmitted = remember { mutableStateOf(false) }
    val isPasswordConfirmationSubmitted = remember { mutableStateOf(false) }
    val showFirstnameField = remember { mutableStateOf(true) }
    val showLastnameField = remember { mutableStateOf(false) }
    val showUsernameField = remember { mutableStateOf(false) }
    val showEmailField = remember { mutableStateOf(false) }
    val showPasswordField = remember { mutableStateOf(false) }
    val showPasswordConfirmationField = remember { mutableStateOf(false) }
    val isEmailValid = remember { mutableStateOf(false) }
    fun isFieldSubmitted(value: String): Boolean {
        return value.contains('\n')
    }
    fun isValidEmail(email: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }


    fun handleRegistration() {
        val userInfo = UserInfos(
            firstname = firstnameState.value,
            lastname = lastnameState.value,
            username = usernameState.value,
            email = emailState.value,
            birth_date = dateOfBirthState.value,
            password = passwordState.value
        )
        CoroutineScope(Dispatchers.IO).launch {
            val response: HttpResponse = httpClient.post("http://10.128.173.52:9002/api/auth/register") {
                contentType(ContentType.Application.Json)
                body = Json.encodeToString(userInfo)
            }

            if (response.status == HttpStatusCode.OK) {
                withContext(Dispatchers.Main) {
                }
            } else {
                withContext(Dispatchers.Main) {
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = sample_test_app.com.R.drawable.logobig),
            contentDescription = null
        )
        Text(text = "Register")
        if (showFirstnameField.value) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                TextField(
                    value = firstnameState.value,
                    onValueChange = { newValue ->
                        firstnameState.value = newValue
                        isFirstnameSubmitted.value = isFieldSubmitted(newValue)
                        if (isFirstnameSubmitted.value) {
                            showFirstnameField.value = false
                            showLastnameField.value = true
                        }
                    },
                    placeholder = { Text("Enter your firstname") },
                    colors = TextFieldDefaults.textFieldColors(backgroundColor = Color.White),
                    modifier = Modifier.padding(vertical = 8.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Button(onClick = {
                        navController.popBackStack()
                    }) {
                        Text("Précédent")
                    }

                    Button(onClick = {
                        showFirstnameField.value = false
                        showLastnameField.value = true
                    }) {
                        Text("Suivant")
                    }
                }
            }
        }

        if (showLastnameField.value) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                TextField(
                    value = lastnameState.value,
                    onValueChange = { newValue ->
                        lastnameState.value = newValue
                        isLastnameSubmitted.value = isFieldSubmitted(newValue)
                        if (isLastnameSubmitted.value) {
                            showLastnameField.value = false
                            showEmailField.value = true                        }
                    },
                    placeholder = { Text("Enter your lastname") },
                    colors = TextFieldDefaults.textFieldColors(backgroundColor = Color.White),
                    modifier = Modifier.padding(vertical = 8.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Button(onClick = {
                        showLastnameField.value = false
                        showFirstnameField.value = true
                    }) {
                        Text("Précédent")
                    }

                    Button(onClick = {
                        showLastnameField.value = false
                        showUsernameField.value = true
                    }) {
                        Text("Suivant")
                    }
                }
            }
        }

        if (showUsernameField.value) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                TextField(
                    value = usernameState.value,
                    onValueChange = { newValue ->
                        usernameState.value = newValue
                        isUsernameSubmitted.value = isFieldSubmitted(newValue)
                        if (isUsernameSubmitted.value) {
                            showUsernameField.value = false
                        }
                    },
                    placeholder = { Text("Enter your username") },
                    colors = TextFieldDefaults.textFieldColors(backgroundColor = Color.White),
                    modifier = Modifier.padding(vertical = 8.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Button(onClick = {
                        showUsernameField.value = false
                        showLastnameField.value = true}) {
                        Text("Précédent")
                    }

                    Button(onClick = {
                        showUsernameField.value = false
                        showEmailField.value = true
                    }) {
                        Text("Suivant")
                    }
                }
            }
        }


        if (showEmailField.value) {
            Text(
                color = Color.White,
                text = "Email"
            )
            TextField(
                value = emailState.value,
                onValueChange = { newValue ->
                    emailState.value = newValue
                    if (!newValue.contains('\n')) {
                        emailState.value = newValue
                    }
                    if (newValue.endsWith("\n")) {
                        emailErrorState.value = !isValidEmail(newValue.trim())
                        if (!emailErrorState.value) {
                            showEmailField.value = false
                            showPasswordField.value = true
                        }
                    }
                },
                placeholder = { Text("Enter your email") },
                colors = TextFieldDefaults.textFieldColors(backgroundColor = Color.White),
                isError = emailErrorState.value,
                modifier = Modifier.padding(vertical = 8.dp)
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(onClick = {
                    showUsernameField.value = true
                    showEmailField.value = false
                }) {
                    Text("Précédent")
                }

                Button(onClick = {
                    showPasswordField.value = true
                    showEmailField.value = false
                }) {
                    Text("Suivant")
                }
            }

            if (emailErrorState.value) {
                Text(
                    color = Color.Red,
                    text = "Invalid email format",
                    modifier = Modifier.padding(top = 4.dp)
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Button(onClick = {
                        showPasswordField.value = false
                        showEmailField.value = true
                    }) {
                        Text("Précédent")
                    }

                    Button(onClick = {
                        if (!emailErrorState.value) {
                            showEmailField.value = false
                            showPasswordField.value = true
                        }
                    }) {
                        Text("Suivant")
                    }
                }
            }
        }

        if (showPasswordField.value) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                TextField(
                    value = passwordState.value,
                    onValueChange = { newValue ->
                        passwordState.value = newValue
                        isPasswordSubmitted.value = isFieldSubmitted(newValue)
                        if (isPasswordSubmitted.value) {
                            showPasswordField.value = false
                            showPasswordConfirmationField.value = true
                        }
                    },
                    placeholder = { Text("Enter your password") },
                    visualTransformation = PasswordVisualTransformation(),
                    colors = TextFieldDefaults.textFieldColors(backgroundColor = Color.White),
                    modifier = Modifier.padding(vertical = 8.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Button(onClick = {
                        showPasswordField.value = false
                        showEmailField.value = true
                    }) {
                        Text("Précédent")
                    }

                    Button(onClick = {
                        showPasswordField.value = false
                        showPasswordConfirmationField.value = true
                    }) {
                        Text("Suivant")
                    }
                }
            }
        }



        if (showPasswordConfirmationField.value) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                TextField(
                    value = passwordConfirmationState.value,
                    onValueChange = { newValue ->
                        passwordConfirmationState.value = newValue
                        if (!newValue.contains('\n')) {
                            passwordConfirmationState.value = newValue
                        }
                        if (newValue.endsWith("\n")) {
                            val passwordMatch = passwordState.value.trim() == passwordConfirmationState.value.trim()
                            if (passwordMatch) {
                                showPasswordConfirmationField.value = false
                                showDateOfBirthField.value = true
                            } else {
                                // Ajoutez ici la logique pour gérer les mots de passe non concordants
                                // Par exemple, afficher un message d'erreur
                            }
                        }
                    },
                    placeholder = { Text("Confirm your password") },
                    visualTransformation = PasswordVisualTransformation(),
                    colors = TextFieldDefaults.textFieldColors(backgroundColor = Color.White),
                    modifier = Modifier.padding(vertical = 8.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Button(onClick = {
                        showPasswordConfirmationField.value = false
                        showPasswordField.value = true
                    }) {
                        Text("Précédent")
                    }

                    Button(onClick = {
                        if (passwordConfirmationState.value.trim() == passwordState.value.trim()) {
                            showPasswordConfirmationField.value = false
                            showDateOfBirthField.value = true
                        } else {
                            // Ajoutez ici la logique pour gérer les mots de passe non concordants
                        }
                    }) {
                        Text("Suivant")
                    }
                }
            }
        }
        val isDateOfBirthSubmitted = remember { mutableStateOf(false) }

        if (showDateOfBirthField.value) {
            Text(
                color = Color.White,
                text = "Date of Birth"
            )
            TextField(
                value = dateOfBirthState.value,
                onValueChange = { newValue ->
                    dateOfBirthState.value = newValue
                    if (!newValue.contains('\n')) {
                        dateOfBirthState.value = newValue
                    }
                    if (newValue.endsWith("\n")) {
                        val dateFormat = SimpleDateFormat("yyyy-MM-dd")
                        dateFormat.isLenient = false
                        try {
                            dateFormat.parse(newValue.trim())
                            isDateOfBirthValid.value = true
                            showDateOfBirthField.value = false
                        } catch (e: Exception) {
                            CoroutineScope(Dispatchers.Main).launch {
                            }
                        }
                    }
                },
                placeholder = { Text("Enter your date of birth") },
                colors = TextFieldDefaults.textFieldColors(backgroundColor = Color.White),
                modifier = Modifier.padding(vertical = 8.dp)
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(onClick = {
                    showPasswordConfirmationField.value = true
                    showDateOfBirthField.value = false
                }) {
                    Text("Précédent")
                }

                Button(onClick = {
                    showPasswordField.value = false
                    showPasswordConfirmationField.value = false
                }) {
                    Text("Suivant")
                }
            }
        }

        if (!showDateOfBirthField.value && isDateOfBirthValid.value) {
            Button(onClick = {
                val userInfo = UserInfos(
                    firstname = firstnameState.value.trim(),
                    lastname = lastnameState.value.trim(),
                    username = usernameState.value.trim(),
                    email = emailState.value.trim(),
                    birth_date = dateOfBirthState.value.trim(),
                    password = passwordState.value.trim()
                )
                val userInfoWrapper = UserInfosWrapper(userInfo)
                val userInfoJson = Json.encodeToString(userInfoWrapper)
                println(userInfoJson)

                CoroutineScope(Dispatchers.IO).launch {
                    val httpClient = HttpClient()
                    try {
                        val response: HttpResponse = httpClient.post("https://3proj-back.tristan-tourbier.com/api/auth/register") {
                            contentType(ContentType.Application.Json)
                            body = userInfoJson
                        }
                        if (response.status == HttpStatusCode.OK) {
                            val responseBody = response.bodyAsText()
                            withContext(Dispatchers.Main) {
                                println("Registration succeeded. Response: $responseBody")
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
}

