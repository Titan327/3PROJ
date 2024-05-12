package sample_test_app.com
import SplashScreen
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import sample_test_app.com.http.Repository.CategoryRepository
import sample_test_app.com.models.CategoryStore
import sample_test_app.com.models.NotificationScreen
import sample_test_app.com.models.User
import sample_test_app.com.ui.screens.GroupListScreen
import sample_test_app.com.ui.screens.GroupScreen
import sample_test_app.com.ui.screens.HomeScreen
import sample_test_app.com.ui.screens.LoginScreen
import sample_test_app.com.ui.screens.MainScreen
import sample_test_app.com.ui.screens.ProfilScreen
import sample_test_app.com.ui.screens.RegisterScreen
import sample_test_app.com.ui.theme.SampleTestAppTheme


class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        CoroutineScope(Dispatchers.IO).launch {
            CategoryStore.categories = CategoryRepository(HttpClient()).getCategories()
        }
        setContent {
            SampleTestAppTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = Color(0xFF1b1b1b)) {
                    AppNavHost()
                }
            }
        }
    }
}

var LocalJwtToken = compositionLocalOf<String> { error("No JWT Token provided") }
var LocalUser = compositionLocalOf<User> { error("No User provided") }

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AppNavHost() {
    val navController = rememberNavController()
    val jwtToken = remember { mutableStateOf("") }
    val user = remember { mutableStateOf(User()) }


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
                composable("groupList") {
                    MainScreen(navController) {
                        GroupListScreen(HttpClient(), navController)
                    }
                }

                composable("profil") {
                    MainScreen(navController) {
                        ProfilScreen(HttpClient(), navController, LocalJwtToken.current)
                    }
                }
                composable("notifications") {
                    MainScreen(navController) {
                        NotificationScreen(HttpClient(), navController)
                    }
                }
                composable("group/{groupId}") { backStackEntry ->
                    GroupScreen(HttpClient(), navController, backStackEntry.arguments?.getString("groupId"))
                }
            }
        }
    }
}

