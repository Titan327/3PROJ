package sample_test_app.com.ui.screens

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Card
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
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
import sample_test_app.com.models.Category
import sample_test_app.com.models.CategoryStore
import sample_test_app.com.models.Group
import sample_test_app.com.models.Transaction
import sample_test_app.com.models.TransactionUser
import sample_test_app.com.models.User
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
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
            transactions.value = TransactionRepository(httpClient).getGroupTransactions(groupId, jwtToken).sortedByDescending { it.date }
        }
    }

    MainScreen(navController = navController,
        groupPicture = if (group.value.picture?.isEmpty() == false) {
            group.value.picture?.get(1)
        } else null) {
        GroupScreenContent(users = users.value, transactions = transactions.value, groupId = groupId.toString(), jwtToken = jwtToken, userId = userId)
    }

}

@RequiresApi(Build.VERSION_CODES.O)
@SuppressLint("DiscouragedApi")
@Composable
fun GroupScreenContent(users: List<User>, transactions: List<Transaction>, groupId: String, jwtToken: String, userId: String) {
    val isBalanceDisplayed = remember { mutableStateOf(true) }
    val isTransactionsDisplayed = remember { mutableStateOf(false) }
    val isRefundDisplayed = remember { mutableStateOf(false) }
    val isPopUpTransactionDisplayed = remember { mutableStateOf(false) }
    val transactionToDisplay = remember { mutableStateOf(Transaction()) }
    val isTransactionCreationPopUpDisplayed = remember { mutableStateOf(false) }
    val isErrorPopUpDisplayed = remember { mutableStateOf(false) }
    val error = remember { mutableStateOf("") }
    val categories = CategoryStore.categories


    Column (
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        // Solde / Transactions / Remboursement
        Row (
            modifier = Modifier
                .clip(shape = RoundedCornerShape(16.dp))
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
                                                        modifier = Modifier
                                                            .align(CenterHorizontally)
                                                            .padding(top = 4.dp)
                                                    )
                                                }

                                                Text(
                                                    if (user.UserGroup.balance != null) { user.UserGroup.balance.toString() + " €" } else { "0 €" },
                                                    modifier = Modifier
                                                        .align(CenterHorizontally)
                                                        .padding(bottom = 4.dp),
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
                if (isTransactionsDisplayed.value) {
                    Row {
                        if (transactions.isEmpty()) {
                            Text(
                                text = "Aucune transaction",
                                color = Color.White,
                                style = TextStyle(
                                    fontSize = 24.sp
                                )
                            )
                        } else {
                            Column {
                                Row (
                                    modifier = Modifier
                                        .fillMaxWidth(),
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    Button(
                                        onClick = {
                                            isTransactionCreationPopUpDisplayed.value = true
                                        },
                                        colors = ButtonDefaults.buttonColors(
                                            backgroundColor = Color(android.graphics.Color.parseColor("#ffa31a")),
                                            contentColor = Color.White
                                        )
                                    ) {
                                        Text("Ajouter une transaction")
                                        Image(
                                            painter = painterResource(id = R.drawable.ic_cross),
                                            contentDescription = "Create Transaction",
                                            modifier = Modifier.size(24.dp)
                                        )
                                    }
                                }
                                Row (
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(bottom = 8.dp)

                                ) {
                                    Column (
                                        modifier = Modifier
                                            .weight(1f),
                                        horizontalAlignment = CenterHorizontally
                                    ) {
                                        Text(
                                            text = "Type",
                                            color = Color.White
                                        )
                                    }
                                    Column (
                                        modifier = Modifier
                                            .weight(3f),
                                        horizontalAlignment = CenterHorizontally
                                    ) {
                                        Text(
                                            text = "Titre",
                                            color = Color.White
                                        )
                                    }
                                    Column (
                                        modifier = Modifier
                                            .weight(2f),
                                        horizontalAlignment = CenterHorizontally
                                    ) {
                                        Text(
                                            text = "Montant",
                                            color = Color.White)
                                    }
                                }
                                Surface(
                                    color = Color(R.color.accent),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(bottom = 16.dp)
                                        .height(1.dp),
                                    elevation = 1.dp,
                                    border = BorderStroke(1.dp, Color(R.color.accent))
                                ) {}
                                for (transaction in transactions) {
                                    Row (
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable {
                                                isPopUpTransactionDisplayed.value =
                                                    true; transactionToDisplay.value = transaction
                                            },
                                    ){
                                        Column (
                                            modifier = Modifier
                                                .weight(1f),
                                            horizontalAlignment = CenterHorizontally,
                                        ) {
                                            val category = categories.find { it.id == transaction.categoryId }
                                            if (category?.icon != null) {
                                                val resourceId = LocalContext.current.resources.getIdentifier(
                                                    category.icon, "drawable", LocalContext.current.packageName
                                                )
                                                Image(
                                                    painter = rememberAsyncImagePainter(
                                                        ImageRequest.Builder(LocalContext.current)
                                                            .data(data = resourceId)
                                                            .build()
                                                    ),
                                                    contentDescription = "Category Icon",
                                                    modifier = Modifier
                                                        .size(60.dp)
                                                        .padding(4.dp)
                                                        .clip(shape = androidx.compose.foundation.shape.CircleShape)
                                                        .background(
                                                            Color(
                                                                android.graphics.Color.parseColor(
                                                                    category.color
                                                                )
                                                            )
                                                        )
                                                )
                                            }
                                        }
                                        Column (
                                            modifier = Modifier
                                                .weight(3f),
                                            horizontalAlignment = CenterHorizontally,
                                        ) {
                                            Text(
                                                text = transaction.label.toString(),
                                                color = Color.White
                                            )
                                        }
                                        Column (
                                            modifier = Modifier
                                                .weight(2f),
                                            horizontalAlignment = CenterHorizontally,
                                        ) {
                                            Text(
                                                text = transaction.total_amount.toString() + " €",
                                                color = Color.White)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (isPopUpTransactionDisplayed.value) {
        AlertDialog(
            onDismissRequest = { isPopUpTransactionDisplayed.value = false },
            title = {
                Row {
                    Text(
                        transactionToDisplay.value.label.toString(),
                        color = Color.White,
                        style = TextStyle(
                            fontSize = 24.sp
                        )
                    )
                }
            },
            text = {
                Column (
                    modifier = Modifier
                        .padding(bottom = 16.dp, top = 16.dp),
                ) {
                    Text(
                        "Montant : " + transactionToDisplay.value.total_amount.toString() + " €",
                        color = Color.White,
                        style = TextStyle(
                            fontSize = 16.sp
                        )
                    )
                    Text(
                        "Catégorie : " + categories.find { it.id == transactionToDisplay.value.categoryId }?.label,
                        color = Color.White,
                        style = TextStyle(
                            fontSize = 16.sp
                        )
                    )
                    Text(
                        "Créé par : " + users.find { it.id == transactionToDisplay.value.senderId }?.username  + " le " + ZonedDateTime.parse(transactionToDisplay.value.date).format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                        color = Color.White,
                        style = TextStyle(
                            fontSize = 16.sp
                        )
                    )
                    for (user in transactionToDisplay.value.TransactionUsers!!) {
                        Row (
                            modifier = Modifier
                                .padding(top = 16.dp, bottom = 16.dp)
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            if (users.find { it.id == user.userId }?.profile_picture?.get(0)
                                    ?.isNotBlank() == true && users.find { it.id == user.userId }?.profile_picture?.get(
                                    0
                                ) != "null"
                            ) {
                                Image(
                                    painter = rememberAsyncImagePainter(
                                        ImageRequest.Builder(LocalContext.current)
                                            .data(
                                                data = users.find { it.id == user.userId }?.profile_picture?.get(
                                                    0
                                                )
                                            )
                                            .apply(block = fun ImageRequest.Builder.() {
                                                transformations(CircleCropTransformation())
                                            }).build()
                                    ),
                                    contentDescription = "User Profile Picture",
                                    modifier = Modifier
                                        .size(40.dp)
                                )
                            } else {
                                Image(
                                    painter = rememberAsyncImagePainter(
                                        ImageRequest.Builder(LocalContext.current)
                                            .data(data = R.drawable.userdefault)
                                            .apply(block = fun ImageRequest.Builder.() {
                                                transformations(CircleCropTransformation())
                                            }).build()
                                    ),
                                    contentDescription = "User Profile Picture",
                                    modifier = Modifier
                                        .size(40.dp)
                                )
                            }
                            val username = users.find { it.id == user.userId }?.username
                            if (username != null) {
                                Text(
                                    text = if (username.length > 11) username.substring(0, 8) + "..." else username.toString(),
                                    color = Color.White,
                                    style = TextStyle(
                                        fontSize = 16.sp
                                    )
                                )
                            }
                            Text(
                                text = user.amount.toString() + " €",
                                color = Color.White,
                                style = TextStyle(
                                    fontSize = 16.sp
                                )
                            )

                        }
                        Surface(
                                color = Color(android.graphics.Color.parseColor("#ffa31a")),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(1.dp),
                        elevation = 1.dp,
                            border = BorderStroke(1.dp, Color(android.graphics.Color.parseColor("#ffa31a")))
                        ) {}
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        isPopUpTransactionDisplayed.value = false
                    },
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = Color(android.graphics.Color.parseColor("#ffa31a")),
                        contentColor = Color.White
                    )                ) {
                    Text("Fermer")
                }
            },
            backgroundColor = Color(android.graphics.Color.parseColor("#292929"))
        )
    }

    if (isTransactionCreationPopUpDisplayed.value) {
        val newTransactionLabel = remember { mutableStateOf("") }
        val newTransactionTotalAmount = remember { mutableStateOf(0.0f) }
        val newTransactionDate = remember { mutableStateOf("") }
        val newTransactionReceipt = remember { mutableStateOf("") }
        val newTransactionCategoryId = remember { mutableStateOf(0.0f) }
        val newTransactionDetails = remember { mutableStateOf(emptyList<TransactionUser>()) }

        fun resetTransactionCreationPopUp() {
            newTransactionLabel.value = ""
            newTransactionTotalAmount.value = 0.0f
            newTransactionDate.value = ""
            newTransactionReceipt.value = ""
            newTransactionCategoryId.value = 0.0f
            newTransactionDetails.value = emptyList()
        }

        AlertDialog (
            onDismissRequest = { isPopUpTransactionDisplayed.value = false },
            title = {
                Text (text = "Créer une transaction", color = Color.White)
            },
            text = {
                Column (
                    modifier = Modifier
                        .padding(bottom = 16.dp, top = 16.dp)
                        .verticalScroll(rememberScrollState())

                ) {
                    val orangeTextFieldColors = TextFieldDefaults.textFieldColors(
                        backgroundColor = Color.White,
                        focusedIndicatorColor = Color.White,
                        unfocusedIndicatorColor = Color.White,
                        cursorColor = Color(android.graphics.Color.parseColor("#ffa31a")),
                        focusedLabelColor = Color(android.graphics.Color.parseColor("#ffa31a")),
                    )
                    Row {
                        if (isErrorPopUpDisplayed.value) {
                            Text(
                                text = error.value,
                                color = Color.Red,
                                modifier = Modifier
                                    .padding(16.dp)
                            )
                        }
                    }
                    TextField(
                        value = newTransactionLabel.value,
                        onValueChange = { newTransactionLabel.value = it },
                        label = { Text("Titre") },
                        colors = orangeTextFieldColors
                    )
                    var expanded by remember { mutableStateOf(false) }
                    var selectedCategory by remember { mutableStateOf<Category?>(null) }

                    Box {
                        TextField(
                            value = selectedCategory?.label ?: "Catégorie",
                            onValueChange = { },
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { expanded = true }
                                .onFocusChanged { if (it.isFocused) expanded = true }
                                .focusable(false),
                            colors = orangeTextFieldColors
                        )
                        DropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false },
                        ) {
                            categories.forEach { category ->
                                DropdownMenuItem(onClick = {
                                    newTransactionCategoryId.value = category.id?.toFloat() ?: 0.0f
                                    selectedCategory = category
                                    expanded = false
                                }) {
                                    category.label?.let { Text(text = it) }
                                }
                            }
                        }
                    }
                    TextField(
                        value = newTransactionTotalAmount.value.toString(),
                        onValueChange = { newTransactionTotalAmount.value = it.toFloat() },
                        label = { Text("Montant") },
                        colors = orangeTextFieldColors,
                        readOnly = true
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        var succes = ""
                        CoroutineScope(Dispatchers.Main).launch {
                            succes = TransactionRepository(HttpClient()).createTransaction(groupId.toDouble(), jwtToken, newTransactionLabel.toString(), newTransactionTotalAmount.value, newTransactionDate.value, "", userId.toDouble(), newTransactionCategoryId.value.toDouble(), newTransactionDetails.value)
                            if (succes == "true") {
                                resetTransactionCreationPopUp()
                                isTransactionCreationPopUpDisplayed.value = false
                                isErrorPopUpDisplayed.value = false
                                error.value = ""
                            } else {
                                isErrorPopUpDisplayed.value = true
                                error.value = succes
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = Color(android.graphics.Color.parseColor("#ffa31a")),
                        contentColor = Color.White
                    )
                ) {
                    Text("Valider")
                }
            },
            dismissButton = {
                Button(
                    onClick = {
                        resetTransactionCreationPopUp()
                        isErrorPopUpDisplayed.value = false
                        error.value = ""
                        isTransactionCreationPopUpDisplayed.value = false
                    },
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = Color(android.graphics.Color.parseColor("#ffa31a")),
                        contentColor = Color.White
                    )
                ) {
                    Text("Fermer")
                }
            },
            backgroundColor = Color(android.graphics.Color.parseColor("#292929"))
        )
    }
}