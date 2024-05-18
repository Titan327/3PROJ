package sample_test_app.com.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import fetchPaymentMethods
import io.ktor.client.HttpClient
import io.ktor.client.request.header
import io.ktor.client.request.put
import io.ktor.client.statement.HttpResponse
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
import sample_test_app.com.LocalUser
import sample_test_app.com.ui.component.ImageChangeSection
import sample_test_app.com.ui.component.PaymentForm
import sample_test_app.com.ui.component.UserStatistics

@OptIn(InternalAPI::class)
@Composable
fun ProfilScreen(httpClient: HttpClient, navController: NavHostController, jwtToken: String, userId: Int) {
    val user = LocalUser.current

    var usernameState = remember { mutableStateOf(user.username ?: "") }
    var firstnameState = remember { mutableStateOf(user.firstname ?: "") }
    var lastnameState = remember { mutableStateOf(user.lastname ?: "") }
    var emailState = remember { mutableStateOf(user.email ?: "") }
    var birthDateState = remember { mutableStateOf(user.birth_date ?: "") }
    var currentPasswordState = remember { mutableStateOf("") }
    var newPasswordState = remember { mutableStateOf("") }

    var repeatNewPasswordState = remember { mutableStateOf("") }

    val isUserInfoDisplayed = remember { mutableStateOf(true) }
    val isImageChangeDisplayed = remember { mutableStateOf(false) }
    val isPasswordChangeDisplayed = remember { mutableStateOf(false) }



    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center, // Center vertically
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Menu
        Row(
            modifier = Modifier
                .clip(shape = RoundedCornerShape(16.dp))
                .fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .weight(1F)
                    .clickable {
                        isUserInfoDisplayed.value = true
                        isImageChangeDisplayed.value = false
                        isPasswordChangeDisplayed.value = false
                    },
                horizontalAlignment = CenterHorizontally
            ) {
                Text(
                    text = "User Info",
                    color = if (isUserInfoDisplayed.value) Color(android.graphics.Color.parseColor("#FFA31A")) else Color.White,
                )
            }
            Column(
                modifier = Modifier
                    .weight(1F)
                    .clickable {
                        isUserInfoDisplayed.value = false
                        isImageChangeDisplayed.value = true
                        isPasswordChangeDisplayed.value = false
                    },
                horizontalAlignment = CenterHorizontally
            ) {
                Text(
                    text = "Image Change",
                    color = if (isImageChangeDisplayed.value) Color(android.graphics.Color.parseColor("#FFA31A")) else Color.White,
                )
            }
            Column(
                modifier = Modifier
                    .weight(1F)
                    .clickable {
                        isUserInfoDisplayed.value = false
                        isImageChangeDisplayed.value = false
                        isPasswordChangeDisplayed.value = true
                    },
                horizontalAlignment = CenterHorizontally
            ) {
                Text(
                    text = "Password Change",
                    color = if (isPasswordChangeDisplayed.value) Color(android.graphics.Color.parseColor("#FFA31A")) else Color.White,
                )
            }
        }

        if (isUserInfoDisplayed.value) {
            TextField(
                value = usernameState.value,
                onValueChange = { usernameState.value = it },
                label = { Text("Username") },
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
                value = firstnameState.value,
                onValueChange = { firstnameState.value = it },
                label = { Text("Prénom") },
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
                value = lastnameState.value,
                onValueChange = { lastnameState.value = it },
                label = { Text("Name") },
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
                value = emailState.value,
                onValueChange = { emailState.value = it },
                label = { Text("Email") },
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
                value = birthDateState.value,
                onValueChange = { birthDateState.value = it },
                label = { Text("Date de naissance") },
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
                val userInfo = mapOf(
                    "userInfos" to mapOf(
                        "username" to usernameState.value,
                        "firstname" to firstnameState.value,
                        "lastname" to lastnameState.value,
                        "email" to emailState.value,
                        "birth_date" to birthDateState.value
                    )
                )

                val userInfoJson = Json.encodeToString(userInfo)

                CoroutineScope(Dispatchers.Main).launch {
                    try {
                        val response: HttpResponse = withContext(Dispatchers.IO) {
                            httpClient.put("https://3proj-back.tristan-tourbier.com/api/users/${user.id}") {
                                contentType(ContentType.Application.Json)
                                header("Authorization", "Bearer $jwtToken")
                                body = userInfoJson
                            }
                        }
                        if (response.status == HttpStatusCode.OK) {
                            withContext(Dispatchers.Main) {
                                println("User update request succeeded.")
                            }
                        } else {
                            withContext(Dispatchers.Main) {
                                println("User update request failed. Error code: ${response.status.value}")
                            }
                        }
                    } catch (e: Exception) {
                        withContext(Dispatchers.Main) {
                            println("An error occurred while sending the request: ${e.message}")
                        }
                    }
                }
            }) {
                Text("Mettre à jour l'utilisateur")
            }
        }

        // Image Section
        if (isImageChangeDisplayed.value) {
            Text("Changement d'image", style = MaterialTheme.typography.h6)
            ImageChangeSection(httpClient, jwtToken)
        }

        // Password Section
        if (isPasswordChangeDisplayed.value) {
            Text("Changement de mot de passe", style = MaterialTheme.typography.h6)
            PasswordChangeSection(currentPasswordState, newPasswordState, repeatNewPasswordState, httpClient, jwtToken)
        }

        PaymentForm(httpClient, jwtToken)


        val paymentMethods = fetchPaymentMethods(httpClient, jwtToken)
        paymentMethods.forEach { paymentMethod ->
            when (paymentMethod.type) {
                "RIB" -> {
                    Spacer(modifier = Modifier.height(16.dp)) // Add a spacer of 16dp height
                    Box {
                        Image(
                            painter = painterResource(id = sample_test_app.com.R.drawable.rib_card),
                            contentDescription = "RIB Card",
                            modifier = Modifier.fillMaxWidth()
                        )
                        Column(
                            modifier = Modifier
                                .align(Alignment.Center) // Change alignment to center
                                .padding(16.dp)
                        ) {
                            Text(text = "${paymentMethod.value.name} ${paymentMethod.value.surname}", color = Color.White)
                            Text(text = "${paymentMethod.value.IBAN}", color = Color.White)
                        }
                    }
                }
                "Paypal" -> {
                    Spacer(modifier = Modifier.height(16.dp)) // Add a spacer of 16dp height
                    Box {
                        Image(
                            painter = painterResource(id = sample_test_app.com.R.drawable.paypal_card1),
                            contentDescription = "Paypal Card",
                            modifier = Modifier.fillMaxWidth()
                        )
                        Column(
                            modifier = Modifier
                                .align(Alignment.Center) // Change alignment to center
                                .padding(16.dp)
                        ) {
                            Text(text = "${paymentMethod.value.user_paypal}", color = Color.White)
                        }
                    }
                }
            }
        }
        UserStatistics(httpClient, jwtToken)}

    }
