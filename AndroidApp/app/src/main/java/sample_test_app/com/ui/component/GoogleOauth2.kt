import android.util.Log
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import com.stevdzasan.onetap.OneTapSignInWithGoogle
import com.stevdzasan.onetap.rememberOneTapSignInState

@Composable
fun GoogleOauthBtn() {
    val state = rememberOneTapSignInState()
    OneTapSignInWithGoogle(
        state = state,
        clientId = "YOUR_CLIENT_ID",
        onTokenIdReceived = { tokenId ->
            Log.d("google-log", tokenId)
        },
        onDialogDismissed = { message ->
            Log.d("google-log", message)
        }
    )

    Button(onClick = { state.open() }) {
        Text(text = "Sign in with google")
    }
}
