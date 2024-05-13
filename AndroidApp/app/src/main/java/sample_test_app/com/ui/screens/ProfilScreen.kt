package sample_test_app.com.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import coil.compose.rememberImagePainter
import io.ktor.client.request.*
import io.ktor.client.statement.HttpResponse
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.ui.Alignment
import io.ktor.client.HttpClient
import io.ktor.client.request.header
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.serialization.json.Json
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import io.ktor.util.InternalAPI
import kotlinx.serialization.encodeToString
import sample_test_app.com.LocalUser

@OptIn(InternalAPI::class)
@Composable
fun ProfilScreen(httpClient: HttpClient, navController: NavHostController, jwtToken: String) {
    val user = LocalUser.current

    var usernameState = remember { mutableStateOf(user.username ?: "") }
    var firstnameState = remember { mutableStateOf(user.firstname ?: "") }
    var lastnameState = remember { mutableStateOf(user.lastname ?: "") }
    var emailState = remember { mutableStateOf(user.email ?: "") }
    var birthDateState = remember { mutableStateOf(user.birth_date ?: "") }
    var selectedImageUri = remember { mutableStateOf<Uri?>(null) }
    val context = LocalContext.current
    val selectImageLauncher = rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent()) { uri: Uri? ->
        selectedImageUri.value = uri
    }

    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxSize(), // Fill the maximum size of the parent
        verticalArrangement = Arrangement.Center, // Center vertically
        horizontalAlignment = Alignment.CenterHorizontally // Center horizontally
    ) {
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
            label = { Text("First Name") },
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
            label = { Text("Last Name") },
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
            label = { Text("Birth Date") },
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
            Text("Update User")

        }
        Button(onClick = { selectImageLauncher.launch("image/*") }) {
            Text("Select Image")
        }
        selectedImageUri.value?.let { uri ->
            Image(
                painter = rememberImagePainter(data = uri),
                contentDescription = "Selection de l'image",
                modifier = Modifier.size(200.dp)
            )
        }


        Button(onClick = {
            selectedImageUri.value?.let { uri ->
                CoroutineScope(Dispatchers.Main).launch {
                    try {
                        val response: HttpResponse = withContext(Dispatchers.IO) {
                            httpClient.post("https://3proj-back.tristan-tourbier.com/api/img/upload/profile-picture") {
                                header("Authorization", "Bearer $jwtToken")
                                body = MultiPartFormDataContent(formData {
                                    val inputStream = context.contentResolver.openInputStream(uri)!!
                                    val bytes = inputStream.readBytes()
                                    inputStream.close()
                                    append(
                                        key = "image",
                                        bytes,
                                        Headers.build {
                                            append(HttpHeaders.ContentDisposition, "form-data; name=image; filename=\"${uri.lastPathSegment}\"")
                                            append(HttpHeaders.ContentType, ContentType.Image.PNG.toString())
                                        }
                                    )
                                })
                            }
                        }
                        if (response.status == HttpStatusCode.OK) {
                            withContext(Dispatchers.Main) {
                                println("Image upload request succeeded.")
                            }
                        } else {
                            withContext(Dispatchers.Main) {
                                println("Image upload request failed. Error code: ${response.status.value}")
                            }
                        }
                    } catch (e: Exception) {
                        withContext(Dispatchers.Main) {
                            println("An error occurred while sending the request: ${e.message}")
                        }
                    }
                }
            }
        }) {
            Text("Upload Image")
        }
    }
}