package sample_test_app.com.models;

import Notification
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.navigation.NavController
import io.ktor.client.HttpClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import sample_test_app.com.LocalJwtToken
import sample_test_app.com.LocalUser


@Composable
fun HomeScreen(httpClient: HttpClient, navController: NavController) {
    val jwtToken = LocalJwtToken.current
    val userId = LocalUser.current.id.toString()
    val notifications = remember { mutableStateOf(emptyList<Notification>()) }

    LaunchedEffect(key1 = userId) {
        CoroutineScope(Dispatchers.Main).launch {
            notifications.value = NotificationRepository(httpClient).getNotifications(userId, jwtToken)
        }
    }
}