package sample_test_app.com.ui.screens

import android.content.Intent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Card
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
import androidx.compose.ui.text.style.TextOverflow
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

    LaunchedEffect(key1 = userId) {
        CoroutineScope(Dispatchers.Main).launch {
            group.value = groupId?.let { GroupRepository(httpClient).getGroup(it, jwtToken) }!!
            users.value = group.value.Users
        }
    }

    MainScreen(navController = navController,
        groupPicture = if (!group.value.picture?.isEmpty()!!) {
        group.value.picture?.get(1)
    } else null) {
        GroupScreenContent(group = group.value, users = users.value, navController = navController, groupId = groupId)
    }

}

@Composable
fun GroupScreenContent(group: Group, users: List<User>, navController: NavController, groupId: String?) {
    val isBalanceDisplayed = remember { mutableStateOf(true) }
    val isTransactionsDisplayed = remember { mutableStateOf(false) }
    val isRefundDisplayed = remember { mutableStateOf(false) }

    Column (
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        // Solde / Transactions / Remboursement
        Row (
            modifier = Modifier
                .clip(shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp))
                .background(color = Color(0xFF808080))
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Column {
                // Titre
                Row {
                    Column(
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
                    Column(
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
                    Column(
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

                Row (
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp, bottom = 16.dp)
                ) {
                    Surface(
                        color = Color.White,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(2.dp),
                        elevation = 2.dp,
                        border = BorderStroke(1.dp, Color.White)
                    ) {}
                }


                // Solde
                if (isBalanceDisplayed.value) {
                    val rowNumber = if (users.size % 3 == 0) {
                        users.size / 3
                    } else {
                        users.size / 3 + 1
                    }
                    val boxHeight = rowNumber * 170
                    Box (modifier = Modifier.height(boxHeight.dp)) {
                        LazyColumn {
                            items(users.chunked(3)) { rowUsers ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 8.dp, bottom = 8.dp),
                                    horizontalArrangement = Arrangement.SpaceEvenly
                                ) {
                                    for (user in rowUsers) {
                                        Card (
                                            backgroundColor = Color.Black
                                        ) {
                                            Column {
                                                Image(
                                                    painter = if (user.profile_picture?.isEmpty() == false) {
                                                        rememberAsyncImagePainter(
                                                            ImageRequest.Builder(LocalContext.current)
                                                                .data(data = user.profile_picture[0])
                                                                .build()
                                                        )
                                                    } else {
                                                        rememberAsyncImagePainter(
                                                            ImageRequest.Builder(LocalContext.current)
                                                                .data(data = R.drawable.userdefault)
                                                                .build()
                                                        )
                                                    },
                                                    contentDescription = "User Picture",
                                                    modifier = Modifier.size(100.dp)
                                                )

                                                user.username?.let {
                                                    val truncatedUsername = if (it.length > 11) it.substring(0, 8) + "..." else it
                                                    Text(
                                                        text = truncatedUsername,
                                                        color = Color.White,
                                                        maxLines = 1,
                                                        overflow = TextOverflow.Ellipsis,
                                                        modifier = Modifier.align(CenterHorizontally)
                                                            .padding(top = 4.dp)
                                                    )
                                                }

                                                Text(
                                                    if (user.UserGroup.balance != null) { user.UserGroup.balance.toString() + " €" } else { "0 €" },
                                                    modifier = Modifier.align(CenterHorizontally).padding(bottom = 4.dp),
                                                    color = Color.White
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // Transactions

            }
        }
    }
}
