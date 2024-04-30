package sample_test_app.com
import SplashScreen
import UserScreen
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import io.ktor.client.HttpClient
import sample_test_app.com.ui.screens.InformationScreen
import sample_test_app.com.ui.screens.LoginScreen
import sample_test_app.com.ui.screens.RegisterScreen
import sample_test_app.com.ui.screens.HomeScreen
import sample_test_app.com.ui.screens.TaskListScreen
import sample_test_app.com.ui.screens.TaskScreen
import sample_test_app.com.ui.screens.WeatherScreen
import sample_test_app.com.ui.theme.SampleTestAppTheme
import io.ktor.client.*
import io.ktor.http.*
import sample_test_app.com.ui.screens.GroupScreen
import sample_test_app.com.ui.screens.ProfilScreen


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