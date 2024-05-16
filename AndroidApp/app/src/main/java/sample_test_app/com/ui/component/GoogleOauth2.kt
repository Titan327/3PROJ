import android.credentials.GetCredentialException
import android.util.Log
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.navigation.NavHostController
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import sample_test_app.com.http.Repository.OauthGoogleRepositry
import sample_test_app.com.http.Security.JwtUtils.JwtUtils
import sample_test_app.com.models.User
import sample_test_app.com.ui.screens.TokenResponse

@Composable
fun GoogleSignInButton (navController: NavHostController, httpClient: HttpClient, jwtToken: MutableState<String>, user: MutableState<User>,) {

    val oauthGoogleRepositry = OauthGoogleRepositry(httpClient)

    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    val onClick: () -> Unit = {
        val credentialManager = CredentialManager.create(context)

        val googleIdOption: GetGoogleIdOption = GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(false)
            .setServerClientId("1081302926939-apm23gnd9muarl2j6j4l24labnkt6e3r.apps.googleusercontent.com")
            //.setNonce("xcvbnvbn,")
            .build()

        val request: GetCredentialRequest = GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()

        coroutineScope.launch {
           try {
               val result = credentialManager.getCredential(
                   request = request,
                   context = context,
               )
               val credential = result.credential
               

               val googleIdCredential = GoogleIdTokenCredential
                   .createFrom(credential.data)

               val googleIdToken = googleIdCredential.idToken

               val response = oauthGoogleRepositry.PostTokenGoogle(googleIdToken)


               if (response != null) {
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
               }






               Log.i("google-oauth",googleIdToken)
           }catch (e:Exception) {
               Log.i("google-oauth", "Error retrieving credentials: " + e.message);
           }


        }

    }

    Button(onClick = onClick) {
        Text(text = "Sign in with Google")
    }
}