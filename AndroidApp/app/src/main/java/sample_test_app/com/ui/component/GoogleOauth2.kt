import android.credentials.GetCredentialException
import android.util.Log
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import io.ktor.client.HttpClient
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import sample_test_app.com.http.Repository.OauthGoogleRepositry

@Composable
fun GoogleSignInButton (httpClient: HttpClient) {

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

               oauthGoogleRepositry.PostTokenGoogle(googleIdToken)

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