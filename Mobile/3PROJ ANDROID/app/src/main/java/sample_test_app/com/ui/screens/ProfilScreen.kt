package sample_test_app.com.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberImagePainter
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.HttpResponse
import io.ktor.client.utils.EmptyContent.headers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.rememberImagePainter
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.header
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

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import coil.compose.rememberImagePainter
import io.ktor.util.InternalAPI
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

@OptIn(InternalAPI::class)
@Composable
fun ProfilScreen(httpClient: HttpClient, userId: String, jwtToken: String) {
    var usernameState = remember { mutableStateOf("") }
    var profilePictureState = remember { mutableStateOf("") }
    var firstnameState = remember { mutableStateOf("") }
    var lastnameState = remember { mutableStateOf("") }
    var emailState = remember { mutableStateOf("") }
    var birthDateState = remember { mutableStateOf("") }

    LaunchedEffect(key1 = userId) {
        CoroutineScope(Dispatchers.Main).launch {
            val userInfoResponse: HttpResponse = withContext(Dispatchers.IO) {
                httpClient.get("https://3proj-back.tristan-tourbier.com/api/users/$userId") {
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
                    val firstname = userInfoJson["firstname"]?.jsonPrimitive?.content
                    val lastname = userInfoJson["lastname"]?.jsonPrimitive?.content
                    val email = userInfoJson["email"]?.jsonPrimitive?.content
                    val birthDate = userInfoJson["birth_date"]?.jsonPrimitive?.content

                    if (username != null) {
                        usernameState.value = username
                    }
                    if (profilePicture != null) {
                        profilePictureState.value = profilePicture
                    }
                    if (firstname != null) {
                        firstnameState.value = firstname
                    }
                    if (lastname != null) {
                        lastnameState.value = lastname
                    }
                    if (email != null) {
                        emailState.value = email
                    }
                    if (birthDate != null) {
                        birthDateState.value = birthDate
                    }
                }
            } else {
                withContext(Dispatchers.Main) {
                    println("User info request failed. Error code: ${userInfoResponse.status.value}")
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxSize(), // Fill the maximum size of the parent
        verticalArrangement = Arrangement.Center, // Center vertically
        horizontalAlignment = Alignment.CenterHorizontally // Center horizontally
    ) {
        Image(
            painter = rememberImagePainter(data = profilePictureState.value),
            contentDescription = "Profile Picture",
            modifier = Modifier
                .size(100.dp)
                .clip(CircleShape)
        )
        TextField(
            value = usernameState.value,
            onValueChange = { usernameState.value = it },
            label = { Text("Username") },
            colors = TextFieldDefaults.textFieldColors(
                textColor = Color.Black,
                cursorColor = Color.Black,
                leadingIconColor = Color.Black,
                trailingIconColor = Color.Black,
                backgroundColor = Color.White,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent
            )
        )
        TextField(
            value = firstnameState.value,
            onValueChange = { firstnameState.value = it },
            label = { Text("First Name") },
            colors = TextFieldDefaults.textFieldColors(
                textColor = Color.Black,
                cursorColor = Color.Black,
                leadingIconColor = Color.Black,
                trailingIconColor = Color.Black,
                backgroundColor = Color.White,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent
            )
        )
        TextField(
            value = lastnameState.value,
            onValueChange = { lastnameState.value = it },
            label = { Text("Last Name") },
            colors = TextFieldDefaults.textFieldColors(
                textColor = Color.Black,
                cursorColor = Color.Black,
                leadingIconColor = Color.Black,
                trailingIconColor = Color.Black,
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
            colors = TextFieldDefaults.textFieldColors(
                textColor = Color.Black,
                cursorColor = Color.Black,
                leadingIconColor = Color.Black,
                trailingIconColor = Color.Black,
                backgroundColor = Color.White,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent
            )
        )
        TextField(
            value = birthDateState.value,
            onValueChange = { birthDateState.value = it },
            label = { Text("Birth Date") },
            colors = TextFieldDefaults.textFieldColors(
                textColor = Color.Black,
                cursorColor = Color.Black,
                leadingIconColor = Color.Black,
                trailingIconColor = Color.Black,
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
                        httpClient.put("https://3proj-back.tristan-tourbier.com/api/users/$userId") {
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
            Text("Update User")
        }
    }
}