package sample_test_app.com.ui.component

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.rememberImagePainter
import io.ktor.client.HttpClient
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.http.ContentType
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import io.ktor.util.InternalAPI
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@OptIn(InternalAPI::class)
@Composable
fun ImageChangeSection(httpClient: HttpClient, jwtToken: String) {
    val context = LocalContext.current
    var selectedImageUri = remember { mutableStateOf<Uri?>(null) }
    val selectImageLauncher = rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent()) { uri: Uri? ->
        selectedImageUri.value = uri
    }

    Button(onClick = { selectImageLauncher.launch("image/*") }) {
        Text("Select an Image")
    }
    selectedImageUri.value?.let { uri ->
        Image(
            painter = rememberImagePainter(data = uri),
            contentDescription = "Selected image",
            modifier = Modifier.size(200.dp)
        )
    }

    Button(onClick = {
        selectedImageUri.value?.let { uri ->
            CoroutineScope(Dispatchers.Main).launch {
                try {
                    val response = withContext(Dispatchers.IO) {
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
                    if (response.status.value == 200) {
                        withContext(Dispatchers.Main) {
                            println("Image upload succeeded.")
                        }
                    } else {
                        withContext(Dispatchers.Main) {
                            println("Image upload failed. Error code: ${response.status.value}")
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