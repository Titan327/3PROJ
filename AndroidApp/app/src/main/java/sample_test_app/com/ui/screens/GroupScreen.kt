package sample_test_app.com.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import coil.transform.CircleCropTransformation
import io.ktor.client.HttpClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import sample_test_app.com.LocalJwtToken
import sample_test_app.com.LocalUser
import sample_test_app.com.R
import sample_test_app.com.http.Repository.GroupRepository
import sample_test_app.com.models.Group
import sample_test_app.com.models.Transaction
import sample_test_app.com.models.User

@Composable
fun GroupScreen(httpClient: HttpClient, navController: NavController, groupId: String?) {
    val jwtToken = LocalJwtToken.current
    val userId = LocalUser.current.id.toString()
    val group = remember { mutableStateOf(Group()) }
    val transactions = remember { mutableStateOf(emptyList<Transaction>()) }
    val users = remember { mutableStateOf(emptyList<User>()) }
    val isBalanceDisplayed = remember { mutableStateOf(true) }
    val isTransactionsDisplayed = remember { mutableStateOf(false) }
    val isRefundDisplayed = remember { mutableStateOf(false) }

    LaunchedEffect(key1 = userId) {
        CoroutineScope(Dispatchers.Main).launch {
            group.value = groupId?.let { GroupRepository(httpClient).getGroup(it, jwtToken) }!!
            users.value = group.value.Users
        }
    }

    Column (
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        // Group infos

        Row {
            Column (
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(2F),
                horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally
            ) {
                if (group.value.picture?.isNotEmpty() == true) {
                    Image(
                        painter = rememberAsyncImagePainter(
                            ImageRequest.Builder(LocalContext.current)
                                .data(data = group.value.picture!![1])
                                .apply(block = fun ImageRequest.Builder.() {
                                    transformations(CircleCropTransformation())
                                }).build()
                        ),
                        contentDescription = "Group Picture",
                        modifier = Modifier
                            .size(150.dp)
                            .padding(top = 16.dp)
                            .clickable {
                                navController.navigate("group/$groupId")
                            }
                    )
                } else {
                    Image(
                        painter = rememberAsyncImagePainter(
                            ImageRequest.Builder(LocalContext.current)
                                .data(data = R.drawable.groupslogofull)
                                .apply(block = fun ImageRequest.Builder.() {
                                    transformations(CircleCropTransformation())
                                }).build()
                        ),
                        contentDescription = "Logo",
                        modifier = Modifier
                            .size(150.dp)
                            .padding(top = 16.dp)
                            .clickable {
                                navController.navigate("group/$groupId")
                            }
                    )
                }
            }
            Column (
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(5F)
            ) {

            }
        }

        // Solde / Transactions / Remboursement
        Row (
            modifier = Modifier
                .clip(shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp))
                .background(color = Color(0xFF808080))
                .padding(16.dp)
                .fillMaxWidth()
        ) {

            // Titre
            Row {
                Column (
                    modifier = Modifier
                        .weight(2F)
                        .clickable {
                            isBalanceDisplayed.value = true
                            isTransactionsDisplayed.value = false
                            isRefundDisplayed.value = false
                        },
                    horizontalAlignment = CenterHorizontally
                ) {
                    Text(
                        text = "Solde",
                        color = if (isBalanceDisplayed.value) Color.Black else Color.White,
                    )
                }
                Column (
                    modifier = Modifier
                        .weight(3F)
                        .clickable {
                            isBalanceDisplayed.value = false
                            isTransactionsDisplayed.value = true
                            isRefundDisplayed.value = false
                        },
                    horizontalAlignment = CenterHorizontally
                ) {
                    Text(
                        text = "Transactions",
                        color = if (isTransactionsDisplayed.value) Color.Black else Color.White,
                    )
                }
                Column (
                    modifier = Modifier
                        .weight(3F)
                        .clickable {
                            isBalanceDisplayed.value = false
                            isTransactionsDisplayed.value = false
                            isRefundDisplayed.value = true
                        },
                    horizontalAlignment = CenterHorizontally
                ) {
                    Text(
                        text = "Remboursement",
                        color = if (isRefundDisplayed.value) Color.Black else Color.White,
                    )
                }
            }

            Surface(
                color = Color.White,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(2.dp),
                elevation = 2.dp,
                border = BorderStroke(1.dp, Color.White)
            ) {}

            // Solde
            //if (isBalanceDisplayed.value) {
            //    Column(
            //        modifier = Modifier.fillMaxWidth()
            //    ) {
                    for (user in users.value) {
                        println(user.UserGroup.balance)
                        Row {
                            //Image(
                            //    painter = if (user.profile_picture?.isEmpty() == true) {
                            //        rememberAsyncImagePainter(
                            //            ImageRequest.Builder(LocalContext.current)
                            //                .data(data = user.profile_picture[0])
                            //                .build()
                            //        )
                            //    } else {
                            //        rememberAsyncImagePainter(
                            //            ImageRequest.Builder(LocalContext.current)
                            //                .data(data = R.drawable.userdefault)
                            //                .build()
                            //        )
                            //    },
                            //    contentDescription = "User Picture",
                            //)
                            Text(
                                user.UserGroup.balance.toString()
                            )
                        }
                    }
                }
        //    }
        //}
    }
}