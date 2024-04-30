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
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import sample_test_app.com.ui.screens.InformationScreen
import sample_test_app.com.ui.screens.TaskListScreen
import sample_test_app.com.ui.screens.TaskScreen
import sample_test_app.com.ui.screens.WeatherScreen
import sample_test_app.com.ui.theme.SampleTestAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SampleTestAppTheme {
                // A surface container using the 'background' color from the theme
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colors.background) {
                    AppNavHost()
                }
            }
        }
    }
}

@Composable
fun AppNavHost() {
    val navController = rememberNavController()
    val startDestination = "splash"

    var canGoBack by remember { mutableStateOf(false) }

    DisposableEffect(navController) {
        val listener = NavController.OnDestinationChangedListener { controller, _, _ ->
            canGoBack = controller.previousBackStackEntry != null
        }
        navController.addOnDestinationChangedListener(listener)
        onDispose {
            navController.removeOnDestinationChangedListener(listener)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "My Super App") },
                navigationIcon = if (canGoBack) {
                    {
                        IconButton(onClick = { navController.navigateUp() }) {
                            Icon(
                                imageVector = Icons.Filled.ArrowBack,
                                contentDescription = "Back"
                            )
                        }
                    }
                } else {
                    null
                }
            )
        },
        content = { paddingValues ->
            Column(modifier = Modifier
                .fillMaxSize()
                .padding(bottom = paddingValues.calculateBottomPadding())
                .verticalScroll(rememberScrollState())) {

                NavHost(navController = navController, startDestination = startDestination) {
                    composable("splash") { SplashScreen(navController) }
                    composable("information") { InformationScreen() }
                    composable("user/{userId}") { navBackStackEntry ->
                        UserScreen(navBackStackEntry.arguments?.getString("userId") ?: "")
                    }
                    composable("tasks") { TaskListScreen(navController) }
                    composable("task/{taskId}") { navBackStackEntry ->
                        TaskScreen((navBackStackEntry.arguments?.getString("taskId") ?: "").toInt())
                    }
                    composable("weather") { WeatherScreen() }
                }
            }
        }
    )
}