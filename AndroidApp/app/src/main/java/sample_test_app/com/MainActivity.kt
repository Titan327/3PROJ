package sample_test_app.com
import SplashScreen
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import io.ktor.client.HttpClient
import sample_test_app.com.models.User
import sample_test_app.com.ui.screens.HomeScreen
import sample_test_app.com.ui.screens.LoginScreen
import sample_test_app.com.ui.screens.MainScreen
import sample_test_app.com.ui.screens.RegisterScreen
import sample_test_app.com.ui.theme.SampleTestAppTheme


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SampleTestAppTheme {
                // A surface container using the 'background' color from the theme
                Surface(modifier = Modifier.fillMaxSize(), color = Color(0xFF1b1b1b)) {
                    AppNavHost()
                }
            }
        }
    }
}

var LocalJwtToken = compositionLocalOf<String> { error("No JWT Token provided") }
var LocalUser = compositionLocalOf<User> { error("No User provided") }

@Composable
fun AppNavHost() {
    val navController = rememberNavController()
    var jwtToken = remember { mutableStateOf("") }
    var user = remember { mutableStateOf(User()) }

    CompositionLocalProvider(LocalJwtToken provides jwtToken.value, LocalUser provides user.value) {
        Surface(modifier = Modifier.fillMaxSize(), color = Color(0xFF1b1b1b)) {
            NavHost(navController = navController, startDestination = "splash") {
                composable("splash") { SplashScreen(navController) }
                composable("register") { RegisterScreen(navController, HttpClient()) }
                composable("login") { LoginScreen(navController, HttpClient(), jwtToken, user) }
                composable("home") {
                    MainScreen(navController) {
                        HomeScreen(HttpClient(), navController)
                    }
                }
            }
        }
    }
}

