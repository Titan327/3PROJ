import androidx.compose.material.Text
import androidx.compose.runtime.Composable

@Composable
fun UserScreen(userId: String) {
    Text(text = "Welcome on the user screen for user $userId")
}