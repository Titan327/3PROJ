package sample_test_app.com.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
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
import sample_test_app.com.http.Repository.TransactionRepository
import sample_test_app.com.models.Group
import sample_test_app.com.models.TransactionUser
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.sp


@Composable
fun HomeScreen(httpClient: HttpClient, navController: NavController) {
    val jwtToken = LocalJwtToken.current
    val userId = LocalUser.current.id.toString()
    val groups = remember { mutableStateOf(emptyList<Group>()) }
    val transactions = remember { mutableStateOf(emptyList<TransactionUser>()) }
    val groupRepository = GroupRepository(httpClient)
    val transactionRepository = TransactionRepository(httpClient)

    LaunchedEffect(key1 = userId) {
        CoroutineScope(Dispatchers.Main).launch {
            groups.value = groupRepository.getUserGroups(userId, jwtToken, true)
            transactions.value = transactionRepository.getLastTransactions(userId, jwtToken)
        }
    }

    Column {
        Row (
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
                .clip(RoundedCornerShape(16.dp))
            ){
            Column (
                modifier = Modifier
                    .background(color = androidx.compose.ui.graphics.Color(0xFF808080))
                    .padding(16.dp)
                    .fillMaxWidth()
            ) {
                Row{
                    Text(
                        text = "Mes groupes favoris:",
                        modifier = Modifier.height(64.dp),
                        color = androidx.compose.ui.graphics.Color.White,
                        style = TextStyle(
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            textDecoration = TextDecoration.Underline
                        )
                    )
                }
                Row (
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (groups.value.isEmpty()) {
                        Text(
                            text = "Aucun groupe favori",
                            color = androidx.compose.ui.graphics.Color.White,
                            style = TextStyle(
                                fontSize = 18.sp
                            )
                        )
                    } else {
                        for (group in groups.value) {
                            if (group.picture?.isNotEmpty() == true) {
                                Image(
                                    painter = rememberAsyncImagePainter(
                                        ImageRequest.Builder(LocalContext.current)
                                            .data(data = group.picture[0])
                                            .apply(block = fun ImageRequest.Builder.() {
                                                transformations(CircleCropTransformation())
                                            }).build()
                                    ),
                                    contentDescription = "Group Picture",
                                    modifier = Modifier
                                        .size(80.dp)
                                        .padding(top = 16.dp)
                                        .clickable {
                                            navController.navigate("group/${group.id}")
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
                                        .size(80.dp)
                                        .padding(top = 16.dp)
                                        .clickable {
                                            navController.navigate("group/${group.id}")
                                        }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}