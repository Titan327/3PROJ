import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import kotlin.random.Random

@Composable
fun SplashScreen(navController: NavHostController) {
    val randomUserId by remember { mutableStateOf(Random.nextInt(0, 100)) }

    Column(modifier = Modifier.padding(16.dp)) {
        Text(text = "Welcome on the splash screen")
        Button(onClick = { navController.navigate("information") }) {
            Text(text = "Go to information")
        }

        Button(onClick = { navController.navigate("user/$randomUserId") }) {
            Text(text = "Go to user $randomUserId")
        }

        Button(onClick = { navController.navigate("tasks") }) {
            Text(text = "Go to task list")
        }

        Button(onClick = { navController.navigate("weather") }) {
            Text(text = "Go to weather")
        }
    }
}