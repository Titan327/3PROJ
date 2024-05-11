package sample_test_app.com.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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


@Composable
fun HomeScreen(httpClient: HttpClient, navController: NavController) {
    val jwtToken = LocalJwtToken.current
    val userId = LocalUser.current.id.toString()
    val groups = remember { mutableStateOf(emptyList<Group>()) }
    val transactions = remember { mutableStateOf(emptyList<TransactionUser>()) }
    val totalPaidThisMonth = remember { mutableStateOf(Pair(0.0f, 0)) }
    val restToPay = remember { mutableStateOf(Pair(0.0f, 0)) }
    val groupRepository = GroupRepository(httpClient)
    val transactionRepository = TransactionRepository(httpClient)

    LaunchedEffect(key1 = userId) {
        CoroutineScope(Dispatchers.Main).launch {
            groups.value = groupRepository.getUserGroups(userId, jwtToken, true)
            transactions.value = transactionRepository.getLastTransactions(userId, jwtToken)
            totalPaidThisMonth.value = transactionRepository.getTotalPaidThisMonth(userId, jwtToken)
            restToPay.value = transactionRepository.getRestToPay(userId, jwtToken)
        }
    }

    Column (
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Row (
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
                .clip(RoundedCornerShape(16.dp))
            ){
            Column (
                modifier = Modifier
                    .background(color = Color(0xFF808080))
                    .padding(16.dp)
                    .fillMaxWidth()
            ) {
                Row{
                    Text(
                        text = "Mes groupes favoris:",
                        color = Color.White,
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
                            color = Color.White,
                            style = TextStyle(
                                fontSize = 18.sp
                            )
                        )
                    } else {
                        for (group in groups.value) {
                            val groupId = group.id.toString()
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
                                        .size(100.dp)
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
                                        .size(100.dp)
                                        .padding(top = 16.dp)
                                        .clickable {
                                            navController.navigate("group/$groupId")
                                        }
                                )
                            }
                        }
                    }
                }
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
                .clip(RoundedCornerShape(16.dp))
        ) {
            Column(
                modifier = Modifier
                    .background(color = Color(0xFF808080))
                    .padding(16.dp)
                    .fillMaxWidth()
            ) {
                Row {
                    Text(
                        text = "Dernières transactions:",
                        color = Color.White,
                        style = TextStyle(
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            textDecoration = TextDecoration.Underline
                        )
                    )
                }
                Row (
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        if (transactions.value.isEmpty()) {
                            Row {
                                Text(
                                    text = "Aucune transaction récente",
                                    color = Color.White,
                                    style = TextStyle(
                                        fontSize = 18.sp
                                    )
                                )
                            }
                        } else {
                            Row (
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 8.dp)
                            ) {
                                Column(
                                    modifier = Modifier
                                        .weight(1f)
                                        .align(Alignment.CenterVertically),
                                ) {
                                    Text(text = "Groupe", color = Color.White)
                                }

                                Column(
                                    modifier = Modifier
                                        .weight(2f)
                                        .align(Alignment.CenterVertically),
                                ) {
                                    Text(text = "Dépense", color = Color.White)
                                }

                                Column(
                                    modifier = Modifier
                                        .weight(1f)
                                        .align(Alignment.CenterVertically),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(text = "Montant", color = Color.White)
                                }
                            }
                            Surface(
                                color = Color.White,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(1.dp),
                                elevation = 1.dp,
                                border = BorderStroke(1.dp, Color.White)
                            ) {}
                            for (transaction in transactions.value) {
                                Row (
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(bottom = 8.dp)
                                ) {
                                    Column(
                                        modifier = Modifier
                                            .weight(1f)
                                            .align(Alignment.CenterVertically),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Image(
                                            painter = if (transaction.Transaction?.Group?.picture?.isNotEmpty() == true) {
                                                rememberAsyncImagePainter(
                                                    ImageRequest.Builder(LocalContext.current)
                                                        .data(data = transaction.Transaction.Group.picture[0])
                                                        .apply(block = fun ImageRequest.Builder.() {
                                                            transformations(CircleCropTransformation())
                                                        }).build()
                                                )
                                            } else {
                                                painterResource(id = R.drawable.groupslogofull)
                                            },
                                            contentDescription = "Logo",
                                            modifier = Modifier
                                                .size(60.dp)
                                        )
                                    }

                                    Column(
                                        modifier = Modifier
                                            .weight(2f)
                                            .align(Alignment.CenterVertically),
                                    ) {
                                        Text(text = transaction.Transaction?.label ?: "", color = Color.White)
                                    }

                                    Column(
                                        modifier = Modifier
                                            .weight(1f)
                                            .align(Alignment.CenterVertically),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Text(text = transaction.amount.toString() + "€", color = Color.White)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
                .clip(RoundedCornerShape(16.dp))
        ) {
            Column(
                modifier = Modifier
                    .background(color = Color(0xFF808080))
                    .padding(16.dp)
                    .fillMaxWidth()
            ) {
                Row {
                    Text(
                        text = "Total payé ce mois-ci:",
                        color = Color.White,
                        style = TextStyle(
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            textDecoration = TextDecoration.Underline
                        )
                    )
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(color = Color(R.color.secondary))
                            .padding(16.dp)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.botleftarrow),
                            contentDescription = "Logo",
                            modifier = Modifier.size(30.dp)
                        )
                    }
                    Column (
                        modifier = Modifier.padding(start = 24.dp)
                    ) {
                        if (totalPaidThisMonth.value.first == 0.0f) {
                            Row {
                                Text(
                                    text = "Aucune transaction ce mois-ci",
                                    color = Color.White,
                                    style = TextStyle(
                                        fontSize = 21.sp
                                    )
                                )
                            }
                        } else {
                            Row {
                                Text(
                                    text = "Total: ${totalPaidThisMonth.value.first}€",
                                    color = Color.White,
                                    style = TextStyle(
                                        fontSize = 21.sp
                                    )
                                )
                            }
                            Row {
                                Text(
                                    text = "Nombre de transactions: ${totalPaidThisMonth.value.second}",
                                    color = Color.White,
                                    style = TextStyle(
                                        fontSize = 18.sp
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
                .clip(RoundedCornerShape(16.dp))
        ) {
            Column(
                modifier = Modifier
                    .background(color = Color(0xFF808080))
                    .padding(16.dp)
                    .fillMaxWidth()
            ) {
                Row {
                    Text(
                        text = "Reste à rembourser:",
                        color = Color.White,
                        style = TextStyle(
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            textDecoration = TextDecoration.Underline
                        )
                    )
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(color = Color(R.color.secondary))
                            .padding(16.dp)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.toprightarrow),
                            contentDescription = "Logo",
                            modifier = Modifier.size(30.dp)
                        )
                    }
                    Column (
                        modifier = Modifier.padding(start = 24.dp)
                    ) {
                        if (totalPaidThisMonth.value.first == 0.0f) {
                            Row {
                                Text(
                                    text = "0€ à rembourser",
                                    color = Color.White,
                                    style = TextStyle(
                                        fontSize = 21.sp
                                    )
                                )
                            }
                        } else {
                            Row {
                                Text(
                                    text = "Total: ${restToPay.value.first}€",
                                    color = Color.White,
                                    style = TextStyle(
                                        fontSize = 21.sp
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}