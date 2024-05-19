package sample_test_app.com.models;

import Notification
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import io.ktor.client.HttpClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import sample_test_app.com.LocalJwtToken
import sample_test_app.com.LocalUser
import sample_test_app.com.R
import sample_test_app.com.http.Repository.NotificationRepository
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun NotificationScreen(httpClient: HttpClient, navController: NavController) {
    val jwtToken = LocalJwtToken.current
    val userId = LocalUser.current.id.toString()
    val notifications = remember { mutableStateOf(emptyList<Notification>()) }

    LaunchedEffect(key1 = userId) {
        CoroutineScope(Dispatchers.Main).launch {
            notifications.value = NotificationRepository(httpClient).getNotifications(jwtToken).reversed()
        }
    }

    Column (
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp)
    ) {
        for (notification in notifications.value) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(color = Color(0xFF808080), shape = RoundedCornerShape(16.dp))
                    .clip(RoundedCornerShape(16.dp))
            ) {
                Column (
                    modifier = Modifier
                        .weight(4F)
                        .padding(16.dp)
                ) {
                    Row {
                        Text(
                            text = notification.message.toString(),
                            style = TextStyle(fontSize = 18.sp),
                            color = Color.White
                        )
                    }
                    Row {
                        Text(
                            text = ZonedDateTime.parse(notification.date).format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                            style = TextStyle(fontSize = 15.sp),
                            modifier = Modifier.padding(top = 4.dp),
                            color = Color.White,
                        )
                    }
                }
                Column (
                    modifier = Modifier
                        .weight(1F)
                        .fillMaxWidth()
                        .align(androidx.compose.ui.Alignment.CenterVertically),
                    horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally
                ) {
                    Row (
                        modifier = Modifier
                            .clickable { navController.navigate("notifications") }
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.trash),
                            contentDescription = "Delete Notification",
                            modifier = Modifier
                                .size(40.dp)
                                .clickable {
                                    CoroutineScope(Dispatchers.Main).launch {
                                        NotificationRepository(httpClient).deleteNotification(
                                            jwtToken,
                                            notification._id.toString()
                                        )
                                        notifications.value =
                                            NotificationRepository(httpClient).getNotifications(
                                                jwtToken
                                            )
                                    }
                                }
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.size(24.dp))
        }
    }
}