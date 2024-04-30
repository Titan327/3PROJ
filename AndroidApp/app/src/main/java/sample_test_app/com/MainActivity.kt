package sample_test_app.com
import SplashScreen
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import io.ktor.client.HttpClient
import sample_test_app.com.ui.screens.GroupScreen
import sample_test_app.com.ui.screens.HomeScreen
import sample_test_app.com.ui.screens.LoginScreen
import sample_test_app.com.ui.screens.ProfilScreen
import sample_test_app.com.ui.screens.RegisterScreen
import sample_test_app.com.ui.theme.SampleTestAppTheme


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SampleTestAppTheme {
                // A surface container using the 'background' color from the theme
                Surface(modifier = Modifier.fillMaxSize(), color = Color(0xFF141332)) {
                    AppNavHost()
                }
            }
        }
    }
}

@Composable
fun AppNavHost() {
    val navController = rememberNavController()

    Surface(modifier = Modifier.fillMaxSize(), color = Color(0xFF141332)) {
        NavHost(navController = navController, startDestination = "splash") {
            composable("splash") { SplashScreen(navController) }
            composable("login") { LoginScreen(navController, HttpClient()) }
            composable("ProfilScreen/{userId}/{jwtToken}") { backStackEntry ->
                val userId = backStackEntry.arguments?.getString("userId") ?: ""
                val jwtToken = backStackEntry.arguments?.getString("jwtToken") ?: ""
                ProfilScreen(HttpClient(), userId, jwtToken)
            }
            composable("GroupScreen/{groupId}/{jwtToken}") { backStackEntry ->
                GroupScreen(backStackEntry, HttpClient())
            }
            composable("register") { RegisterScreen(navController , HttpClient()) }
            composable("home/{userId}/{jwtToken}") { backStackEntry ->
                val userId = backStackEntry.arguments?.getString("userId") ?: ""
                val jwtToken = backStackEntry.arguments?.getString("jwtToken") ?: ""
                HomeScreen(userId, HttpClient(), jwtToken, navController)
            }
        }
    }
}