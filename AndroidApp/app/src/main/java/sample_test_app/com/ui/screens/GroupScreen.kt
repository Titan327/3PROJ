package sample_test_app.com.ui.screens

import PaymentMethod
import PaypalPaymentMethod
import RIBPaymentMethod
import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.ClipData
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Card
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.IconButton
import androidx.compose.material.OutlinedTextField
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
import android.content.ClipboardManager
import android.content.Intent
import android.net.Uri
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
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
import sample_test_app.com.http.Repository.PaymentMethodsRepository
import sample_test_app.com.http.Repository.RefundRepository
import sample_test_app.com.http.Repository.TransactionRepository
import sample_test_app.com.models.Category
import sample_test_app.com.models.CategoryStore
import sample_test_app.com.models.Group
import sample_test_app.com.models.Refund
import sample_test_app.com.models.Transaction
import sample_test_app.com.models.TransactionUser
import sample_test_app.com.models.User
import java.time.LocalDate
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun GroupScreen(httpClient: HttpClient, navController: NavController, groupId: String?) {
    val jwtToken = LocalJwtToken.current
    val userId = LocalUser.current.id.toString()
    val group = remember { mutableStateOf(Group()) }

    LaunchedEffect(key1 = userId) {
        CoroutineScope(Dispatchers.Main).launch {
            group.value = groupId?.let { GroupRepository(httpClient).getGroup(it, jwtToken) }!!
        }
    }

    MainScreen(navController = navController,
        groupPicture = if (group.value.picture?.isEmpty() == false) {
            group.value.picture?.get(1)
        } else null) {
        GroupScreenContent(groupId = groupId.toString(), jwtToken = jwtToken, userId = userId, httpClient)
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@SuppressLint("DiscouragedApi")
@Composable
fun GroupScreenContent(groupId: String, jwtToken: String, userId: String, httpClient: HttpClient) {
    val transactions = remember { mutableStateOf(emptyList<Transaction>()) }
    val refundsToDo = remember { mutableStateOf(emptyList<Refund>()) }
    val doneRefunds = remember { mutableStateOf(emptyList<Refund>()) }
    val users = remember { mutableStateOf(emptyList<User>()) }

    val isBalanceDisplayed = remember { mutableStateOf(true) }
    val isTransactionsDisplayed = remember { mutableStateOf(false) }
    val isRefundDisplayed = remember { mutableStateOf(false) }
    val isPopUpTransactionDisplayed = remember { mutableStateOf(false) }
    val transactionDetails = remember { mutableStateOf(Transaction()) }
    val isTransactionCreationPopUpDisplayed = remember { mutableStateOf(false) }
    val isErrorPopUpDisplayed = remember { mutableStateOf(false) }
    val isRefundPopUpDisplayed = remember { mutableStateOf(false) }
    val refundToDoDetails = remember { mutableStateOf(Refund()) }
    val paymentMethodsUserToRefund = remember { mutableStateOf(emptyList<PaymentMethod>()) }
    val error = remember { mutableStateOf("") }
    val categories = CategoryStore.categories

    fun getGroupInfos() {
        CoroutineScope(Dispatchers.Main).launch {
            transactions.value = TransactionRepository(httpClient).getGroupTransactions(groupId, jwtToken).sortedByDescending { it.date }
            refundsToDo.value = RefundRepository(httpClient).getGroupRefunds(jwtToken, groupId)
            doneRefunds.value = RefundRepository(httpClient).getGroupDoneRefunds(jwtToken, groupId)
            users.value = groupId.let { GroupRepository(httpClient).getGroup(it, jwtToken) }.Users
        }
    }

    getGroupInfos()

    if (isTransactionCreationPopUpDisplayed.value) {
        val newTransactionLabel = remember { mutableStateOf("") }
        val newTransactionTotalAmount = remember { mutableStateOf(0.0f) }
        val newTransactionDate = remember { mutableStateOf(LocalDate.now()) }
        val newTransactionReceipt = remember { mutableStateOf("") }
        val newTransactionCategoryId = remember { mutableStateOf(0.0f) }
        val newTransactionDetails = remember { mutableStateOf(emptyList<TransactionUser>()) }

        fun resetTransactionCreationPopUp() {
            newTransactionLabel.value = ""
            newTransactionTotalAmount.value = 0.0f
            newTransactionDate.value = LocalDate.now()
            newTransactionReceipt.value = ""
            newTransactionCategoryId.value = 0.0f
            newTransactionDetails.value = emptyList()
        }

        Box (
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.5f))
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Box (
                modifier = Modifier
                    .clip(shape = RoundedCornerShape(16.dp))
                    .background(color = Color(R.color.background))
                    .padding(8.dp)
                    .fillMaxWidth(),
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxHeight()
                        .wrapContentHeight()
                        .padding(bottom = 16.dp, top = 16.dp)
                ) {
                    val orangeTextFieldColors = TextFieldDefaults.textFieldColors(
                        backgroundColor = Color.White,
                        focusedIndicatorColor = Color.White,
                        unfocusedIndicatorColor = Color.White,
                        cursorColor = Color(android.graphics.Color.parseColor("#ffa31a")),
                        focusedLabelColor = Color(android.graphics.Color.parseColor("#ffa31a")),
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 24.dp),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "Nouvelle transaction",
                            color = Color.White,
                            style = TextStyle(
                                fontSize = 20.sp
                            )
                        )
                    }

                    Row {
                        Column (
                            modifier = Modifier
                                .fillMaxWidth(),
                            horizontalAlignment = CenterHorizontally,
                        ) {
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
                                colors = orangeTextFieldColors,
                                modifier = Modifier.padding(bottom = 16.dp)
                            )
                            var expanded by remember { mutableStateOf(false) }
                            var selectedCategory by remember { mutableStateOf<Category?>(null) }

                            Box {
                                TextField(
                                    value = selectedCategory?.label ?: "Catégorie",
                                    onValueChange = { },
                                    modifier = Modifier
                                        .clickable { expanded = true }
                                        .onFocusChanged { if (it.isFocused) expanded = true }
                                        .focusable(false)
                                        .padding(bottom = 16.dp),
                                    colors = orangeTextFieldColors
                                )
                                DropdownMenu(
                                    expanded = expanded,
                                    onDismissRequest = { expanded = false },
                                ) {
                                    categories.forEach { category ->
                                        DropdownMenuItem(onClick = {
                                            newTransactionCategoryId.value =
                                                category.id?.toFloat() ?: 0.0f
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
                                readOnly = true,
                                modifier = Modifier.padding(bottom = 16.dp)
                            )
                            var dateDialogShown by remember { mutableStateOf(false) }

                            if (dateDialogShown) {
                                DatePickerDialog(
                                    LocalContext.current,
                                    { _, year, month, dayOfMonth ->
                                        newTransactionDate.value =
                                            LocalDate.of(year, month + 1, dayOfMonth)
                                        dateDialogShown = false
                                    },
                                    newTransactionDate.value.year,
                                    newTransactionDate.value.monthValue - 1,
                                    newTransactionDate.value.dayOfMonth
                                ).show()
                            }

                            TextField(
                                value = newTransactionDate.value.format(
                                    DateTimeFormatter.ofPattern(
                                        "dd/MM/yyyy"
                                    )
                                ),
                                onValueChange = {
                                    newTransactionDate.value =
                                        LocalDate.parse(
                                            it,
                                            DateTimeFormatter.ofPattern("yyyy/MM/dd")
                                        )
                                },
                                readOnly = true,
                                label = { Text("Date") },
                                modifier = Modifier
                                    .clickable { dateDialogShown = true }
                                    .padding(bottom = 16.dp),
                                colors = orangeTextFieldColors
                            )
                            for (user in users.value) {
                                Row(
                                    modifier = Modifier
                                        .padding(top = 16.dp, bottom = 16.dp)
                                        .fillMaxWidth(0.75f),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    if (user.profile_picture?.get(0)
                                            ?.isNotBlank() == true && user.profile_picture[0] != "null"
                                    ) {
                                        Image(
                                            painter = rememberAsyncImagePainter(
                                                ImageRequest.Builder(LocalContext.current)
                                                    .data(data = user.profile_picture[0])
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
                                    val username = user.username
                                    if (username != null) {
                                        Text(
                                            text = username,
                                            color = Color.White,
                                            style = TextStyle(
                                                fontSize = 16.sp
                                            )
                                        )
                                    }
                                }
                                Row {
                                    var text by remember { mutableStateOf("") }
                                    OutlinedTextField(
                                        value = text,
                                        onValueChange = { newText ->
                                            if (newText.isEmpty() || newText.toFloatOrNull() != null) {
                                                text = newText
                                            }
                                            if (newTransactionDetails.value.find { it.userId == user.id } != null) {
                                                newTransactionDetails.value =
                                                    newTransactionDetails.value.map {
                                                        if (it.userId == user.id) {
                                                            it.copy(amount = text.toDouble())
                                                        } else {
                                                            it
                                                        }
                                                    }
                                            } else {
                                                newTransactionDetails.value += TransactionUser(
                                                    userId = user.id,
                                                    amount = text.toDouble()
                                                )
                                            }
                                            newTransactionTotalAmount.value =
                                                newTransactionDetails.value.sumOf { it.amount!!.toDouble() }
                                                    .toFloat()
                                        },
                                        label = { Text("Montant") },
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                        colors = orangeTextFieldColors
                                    )
                                }
                            }
                        }
                    }
                    Row (
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 24.dp),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
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
                        Button(
                            onClick = {
                                var succes = ""
                                CoroutineScope(Dispatchers.Main).launch {
                                    succes = TransactionRepository(HttpClient()).createTransaction(
                                        groupId.toInt(),
                                        jwtToken,
                                        newTransactionLabel.value,
                                        newTransactionTotalAmount.value,
                                        newTransactionDate.value,
                                        "",
                                        userId.toInt(),
                                        newTransactionCategoryId.value.toInt(),
                                        newTransactionDetails.value
                                    )
                                    if (succes == "true") {
                                        resetTransactionCreationPopUp()
                                        isTransactionCreationPopUpDisplayed.value = false
                                        getGroupInfos()
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
                    }
                }
            }
        }
    } else {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Solde / Transactions / Remboursement
            Row(
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
                                color = if (isBalanceDisplayed.value) Color(android.graphics.Color.parseColor("#FFA31A")) else Color.White,
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
                                color = if (isTransactionsDisplayed.value) Color(android.graphics.Color.parseColor("#FFA31A")) else Color.White,
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
                                color = if (isRefundDisplayed.value) Color(android.graphics.Color.parseColor("#FFA31A")) else Color.White,
                            )
                        }
                    }

                    Row(
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
                        val rowNumber = if (users.value.size % 3 == 0) {
                            users.value.size / 3
                        } else {
                            users.value.size / 3 + 1
                        }
                        val boxHeight = rowNumber * 170
                        Box(modifier = Modifier.height(boxHeight.dp)) {
                            LazyColumn {
                                items(users.value.chunked(3)) { rowUsers ->
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(top = 8.dp, bottom = 8.dp),
                                        horizontalArrangement = Arrangement.SpaceEvenly
                                    ) {
                                        for (user in rowUsers) {
                                            Card(
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
                                                        val truncatedUsername =
                                                            if (it.length > 11) it.substring(
                                                                0,
                                                                8
                                                            ) + "..." else it
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
                                                        if (user.UserGroup.balance != null) {
                                                            user.UserGroup.balance.toString() + " €"
                                                        } else {
                                                            "0 €"
                                                        },
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
                            if (transactions.value.isEmpty()) {
                                Text(
                                    text = "Aucune transaction",
                                    color = Color.White,
                                    style = TextStyle(
                                        fontSize = 24.sp
                                    )
                                )
                            } else {
                                Column {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth(),
                                        horizontalArrangement = Arrangement.Center
                                    ) {
                                        Button(
                                            onClick = {
                                                isTransactionCreationPopUpDisplayed.value = true
                                            },
                                            colors = ButtonDefaults.buttonColors(
                                                backgroundColor = Color(
                                                    android.graphics.Color.parseColor(
                                                        "#ffa31a"
                                                    )
                                                ),
                                                contentColor = Color.White
                                            ),
                                            modifier = Modifier
                                                .padding(bottom = 16.dp)
                                        ) {
                                            Text("Ajouter une transaction")
                                            Image(
                                                painter = painterResource(id = R.drawable.ic_cross),
                                                contentDescription = "Create Transaction",
                                                modifier = Modifier.size(24.dp)
                                            )
                                        }
                                    }
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(bottom = 8.dp)

                                    ) {
                                        Column(
                                            modifier = Modifier
                                                .weight(1f),
                                            horizontalAlignment = CenterHorizontally
                                        ) {
                                            Text(
                                                text = "Type",
                                                color = Color.White
                                            )
                                        }
                                        Column(
                                            modifier = Modifier
                                                .weight(3f),
                                            horizontalAlignment = CenterHorizontally
                                        ) {
                                            Text(
                                                text = "Titre",
                                                color = Color.White
                                            )
                                        }
                                        Column(
                                            modifier = Modifier
                                                .weight(2f),
                                            horizontalAlignment = CenterHorizontally
                                        ) {
                                            Text(
                                                text = "Montant",
                                                color = Color.White
                                            )
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
                                    for (transaction in transactions.value) {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clickable {
                                                    isPopUpTransactionDisplayed.value =
                                                        true; transactionDetails.value = transaction
                                                },
                                        ) {
                                            Column(
                                                modifier = Modifier
                                                    .weight(1f),
                                                horizontalAlignment = CenterHorizontally,
                                            ) {
                                                val category =
                                                    categories.find { it.id == transaction.categoryId }
                                                if (category?.icon != null) {
                                                    val resourceId =
                                                        LocalContext.current.resources.getIdentifier(
                                                            category.icon,
                                                            "drawable",
                                                            LocalContext.current.packageName
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
                                            Column(
                                                modifier = Modifier
                                                    .weight(3f)
                                                    .align(Alignment.CenterVertically),
                                                horizontalAlignment = CenterHorizontally,
                                            ) {
                                                Text(
                                                    text = transaction.label.toString(),
                                                    color = Color.White,
                                                )
                                            }
                                            Column(
                                                modifier = Modifier
                                                    .weight(2f)
                                                    .align(Alignment.CenterVertically),
                                                horizontalAlignment = CenterHorizontally,
                                            ) {
                                                Text(
                                                    text = transaction.total_amount.toString() + " €",
                                                    color = Color.White
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // Refunds
                    if (isRefundDisplayed.value) {
                        if (doneRefunds.value.isEmpty() && refundsToDo.value.isEmpty()) {
                            Text(
                                text = "Aucun remboursement n'est à faire ou n'a encore été fait dans ce groupe",
                                color = Color.White,
                                style = TextStyle(
                                    fontSize = 20.sp
                                )
                            )
                        } else {
                            Column {
                                if (refundsToDo.value.isNotEmpty()) {
                                    Row (modifier = Modifier.padding(bottom = 8.dp)) {
                                        Text(
                                            text = "Remboursements à faire",
                                            color = Color.White,
                                            style = TextStyle(
                                                fontSize = 24.sp,
                                                textDecoration = TextDecoration.Underline
                                            )
                                        )
                                    }
                                    Row (
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(bottom = 8.dp)
                                    ) {
                                        Column (
                                            modifier = Modifier.weight(2f),
                                            horizontalAlignment = CenterHorizontally
                                        ) {
                                            Text(
                                                text = "Rembourseur",
                                                color = Color.White
                                            )
                                        }
                                        Column (
                                            modifier = Modifier.weight(2f),
                                            horizontalAlignment = CenterHorizontally
                                        ) {
                                            Text(
                                                text = "Remboursé",
                                                color = Color.White
                                            )
                                        }
                                        Column (
                                            modifier = Modifier.weight(2f),
                                            horizontalAlignment = CenterHorizontally
                                        ) {
                                            Text(
                                                text = "Montant",
                                                color = Color.White
                                            )
                                        }
                                        Column (
                                            modifier = Modifier.weight(1f),
                                            horizontalAlignment = CenterHorizontally
                                        ) {
                                            Text(
                                                text = "Action",
                                                color = Color.White
                                            )
                                        }
                                    }
                                    for (refund in refundsToDo.value) {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                        ) {
                                            Column (
                                                modifier = Modifier
                                                    .weight(2f)
                                                    .align(Alignment.CenterVertically),
                                                horizontalAlignment = CenterHorizontally
                                            ) {
                                                Text(
                                                    text = if (users.value.find { it.id == refund.refundingUserId }?.username?.length!! > 11)
                                                        users.value.find { it.id == refund.refundingUserId }?.username.toString().substring(0, 8) + "..."
                                                    else
                                                        users.value.find { it.id == refund.refundingUserId }?.username.toString(),
                                                    color = Color.White)
                                            }
                                            Column (
                                                modifier = Modifier
                                                    .weight(2f)
                                                    .align(Alignment.CenterVertically),
                                                horizontalAlignment = CenterHorizontally
                                            ) {
                                                Text(
                                                    text = if (users.value.find { it.id == refund.refundedUserId }?.username?.length!! > 11)
                                                        users.value.find { it.id == refund.refundedUserId }?.username.toString().substring(0, 8) + "..."
                                                    else
                                                        users.value.find { it.id == refund.refundedUserId }?.username.toString(),
                                                    color = Color.White
                                                )
                                            }
                                            Column (
                                                modifier = Modifier
                                                    .weight(2f)
                                                    .align(Alignment.CenterVertically),
                                                horizontalAlignment = CenterHorizontally
                                            ) {
                                                Text(
                                                    text = refund.amount.toString() + " €",
                                                    color = Color.White
                                                )
                                            }
                                            Column (
                                                modifier = Modifier.weight(1f),
                                                horizontalAlignment = CenterHorizontally
                                            ) {
                                                if (refund.refundingUserId == userId) {
                                                    IconButton(
                                                        onClick =
                                                        {
                                                            isRefundPopUpDisplayed.value = true
                                                            refundToDoDetails.value = refund
                                                            CoroutineScope(Dispatchers.Main).launch {
                                                                paymentMethodsUserToRefund.value =
                                                                    PaymentMethodsRepository(httpClient).getPaymentMethods(
                                                                        refund.refundedUserId.toString(),
                                                                        groupId,
                                                                        jwtToken
                                                                    )
                                                            }
                                                        }
                                                    ) {
                                                        Image(
                                                            painter = painterResource(id = R.drawable.rightarrow),
                                                            contentDescription = "Create Group",
                                                            modifier = Modifier
                                                                .size(24.dp)
                                                                .clip(CircleShape)
                                                                .background(
                                                                    Color(
                                                                        android.graphics.Color.parseColor(
                                                                            "#FFA31A"
                                                                        )
                                                                    )
                                                                )
                                                        )
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }

                                if (doneRefunds.value.isNotEmpty()) {
                                    Row (modifier = Modifier.padding(bottom = 8.dp)) {
                                        Text(
                                            text = "Remboursements déjà effectués: ",
                                            color = Color.White,
                                            style = TextStyle(
                                                fontSize = 24.sp,
                                                textDecoration = TextDecoration.Underline
                                            )
                                        )
                                    }
                                    Row (
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(bottom = 8.dp)
                                    ) {
                                        Column (
                                            modifier = Modifier.weight(1f),
                                            horizontalAlignment = CenterHorizontally
                                        ) {
                                            Text(
                                                text = "Rembourseur",
                                                color = Color.White
                                            )
                                        }
                                        Column (
                                            modifier = Modifier.weight(1f),
                                            horizontalAlignment = CenterHorizontally
                                        ) {
                                            Text(
                                                text = "Remboursé",
                                                color = Color.White
                                            )
                                        }
                                        Column (
                                            modifier = Modifier.weight(1f),
                                            horizontalAlignment = CenterHorizontally
                                        ) {
                                            Text(
                                                text = "Montant",
                                                color = Color.White
                                            )
                                        }
                                        Column (
                                            modifier = Modifier.weight(1f),
                                            horizontalAlignment = CenterHorizontally
                                        ) {
                                            Text(
                                                text = "Date",
                                                color = Color.White
                                            )
                                        }
                                    }
                                    for (refund in doneRefunds.value) {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                        ) {
                                            Column (
                                                modifier = Modifier
                                                    .weight(1f)
                                                    .align(Alignment.CenterVertically),
                                                horizontalAlignment = CenterHorizontally
                                            ) {
                                                Text(
                                                    text = if (users.value.find { it.id == refund.refundingUserId }?.username?.length!! > 11)
                                                        users.value.find { it.id == refund.refundingUserId }?.username.toString().substring(0, 8) + "..."
                                                    else
                                                        users.value.find { it.id == refund.refundingUserId }?.username.toString(),
                                                    color = Color.White)
                                            }
                                            Column (
                                                modifier = Modifier
                                                    .weight(1f)
                                                    .align(Alignment.CenterVertically),
                                                horizontalAlignment = CenterHorizontally
                                            ) {
                                                Text(
                                                    text = if (users.value.find { it.id == refund.refundedUserId }?.username?.length!! > 11)
                                                        users.value.find { it.id == refund.refundedUserId }?.username.toString().substring(0, 8) + "..."
                                                    else
                                                        users.value.find { it.id == refund.refundedUserId }?.username.toString(),
                                                    color = Color.White
                                                )
                                            }
                                            Column (
                                                modifier = Modifier
                                                    .weight(1f)
                                                    .align(Alignment.CenterVertically),
                                                horizontalAlignment = CenterHorizontally
                                            ) {
                                                Text(
                                                    text = String.format("%.2f €", refund.amount),                                                    color = Color.White
                                                )
                                            }
                                            Column (
                                                modifier = Modifier.weight(1f),
                                                horizontalAlignment = CenterHorizontally
                                            ) {
                                                Text(
                                                    text = ZonedDateTime.parse(refund.updatedAt).format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
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
            }
        }

        if (isPopUpTransactionDisplayed.value) {
            AlertDialog(
                onDismissRequest = { isPopUpTransactionDisplayed.value = false },
                title = {
                    Row {
                        Text(
                            transactionDetails.value.label.toString(),
                            color = Color.White,
                            style = TextStyle(
                                fontSize = 24.sp
                            )
                        )
                    }
                },
                text = {
                    Column(
                        modifier = Modifier
                            .padding(bottom = 16.dp, top = 16.dp),
                    ) {
                        Text(
                            "Montant : " + transactionDetails.value.total_amount.toString() + " €",
                            color = Color.White,
                            style = TextStyle(
                                fontSize = 16.sp
                            )
                        )
                        Text(
                            "Catégorie : " + categories.find { it.id == transactionDetails.value.categoryId }?.label,
                            color = Color.White,
                            style = TextStyle(
                                fontSize = 16.sp
                            )
                        )
                        Text(
                            "Créé par : " + users.value.find { it.id == transactionDetails.value.senderId }?.username + " le " + ZonedDateTime.parse(
                                transactionDetails.value.date
                            ).format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                            color = Color.White,
                            style = TextStyle(
                                fontSize = 16.sp
                            )
                        )
                        for (user in transactionDetails.value.TransactionUsers!!) {
                            Row(
                                modifier = Modifier
                                    .padding(top = 16.dp, bottom = 16.dp)
                                    .fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                if (users.value.find { it.id == user.userId }?.profile_picture?.get(0)
                                        ?.isNotBlank() == true && users.value.find { it.id == user.userId }?.profile_picture?.get(
                                        0
                                    ) != "null"
                                ) {
                                    Image(
                                        painter = rememberAsyncImagePainter(
                                            ImageRequest.Builder(LocalContext.current)
                                                .data(
                                                    data = users.value.find { it.id == user.userId }?.profile_picture?.get(
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
                                val username = users.value.find { it.id == user.userId }?.username
                                if (username != null) {
                                    Text(
                                        text = if (username.length > 11) username.substring(
                                            0,
                                            8
                                        ) + "..." else username.toString(),
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
                                border = BorderStroke(
                                    1.dp,
                                    Color(android.graphics.Color.parseColor("#ffa31a"))
                                )
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
                        )
                    ) {
                        Text("Fermer")
                    }
                },
                backgroundColor = Color(android.graphics.Color.parseColor("#292929"))
            )
        }

        if (isRefundPopUpDisplayed.value) {
            AlertDialog(
                onDismissRequest = { isRefundPopUpDisplayed.value = false },
                title = {
                    Row {
                        Text(
                            text = "Rembourser " + refundToDoDetails.value.amount + "€ à " + users.value.find { it.id == refundToDoDetails.value.refundedUserId }?.username,
                            color = Color.White,
                            style = TextStyle(
                                fontSize = 24.sp
                            )
                        )
                    }
                },
                text = {
                    val userToRefundUsername = users.value.find { it.id == refundToDoDetails.value.refundedUserId }?.username
                    Column(
                        modifier = Modifier
                            .padding(bottom = 16.dp, top = 16.dp),
                    ) {
                        if (paymentMethodsUserToRefund.value.isEmpty()) {
                            Text(
                                text = "$userToRefundUsername n'a ajouté aucun moyen de paiement, vous pouvez toujours effectuer un remboursement par moyen externe.",
                                color = Color.White,
                                style = TextStyle(
                                    fontSize = 16.sp
                                )
                            )
                        } else {
                            val userHavePaypal = paymentMethodsUserToRefund.value.any { it is PaypalPaymentMethod }
                            val userHaveRib = paymentMethodsUserToRefund.value.any { it is RIBPaymentMethod }
                            if (userHaveRib) {
                                Text(
                                    text = "Vous pouvez copier l'IBAN de $userToRefundUsername pour effectuer le virement bancaire ensuite ou effectuer le remboursement via PayPal.",
                                    color = Color.White,
                                    modifier = Modifier.padding(bottom = 16.dp)
                                )
                            }
                            if (userHavePaypal) {
                                Text(
                                    text = "En cliquant sur \"Rembourser via Paypal\" vous serez redirigé vers le profil de $userToRefundUsername afin d'effectuer le remboursement.",
                                    color = Color.White,
                                    modifier = Modifier.padding(bottom = 16.dp)
                                )
                            }
                            if (userHaveRib) {
                                val ribPaymentMethod = paymentMethodsUserToRefund.value.find { it is RIBPaymentMethod } as? RIBPaymentMethod
                                val iban = ribPaymentMethod?.value?.IBAN
                                Row (
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(bottom = 16.dp)
                                        .border(
                                            width = 1.dp,
                                            color = Color.White,
                                            shape = RoundedCornerShape(8.dp),
                                        )
                                ) {
                                    Text(
                                        text = iban.toString(),
                                        color = Color.White,
                                        modifier = Modifier.padding(8.dp)
                                    )
                                }
                                Row (
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    val context = LocalContext.current
                                    Button(onClick = {
                                        val clipboard = ContextCompat.getSystemService(context, ClipboardManager::class.java)
                                        val clip = ClipData.newPlainText("IBAN", iban)
                                        clipboard?.setPrimaryClip(clip)
                                    },
                                        colors = ButtonDefaults.buttonColors(
                                            backgroundColor = Color.Transparent,
                                            contentColor = Color.White
                                        ),
                                        modifier = Modifier.border(
                                            1.dp,
                                            Color(android.graphics.Color.parseColor("#FFA31A")),
                                            shape = RoundedCornerShape(8.dp)
                                        )
                                    ) {
                                        Image(
                                            painter = painterResource(id = R.drawable.orangecopy),
                                            contentDescription = "Image Description",
                                            modifier = Modifier.size(24.dp)
                                        )
                                    }
                                    if (userHavePaypal) {
                                        Button(
                                            onClick = {
                                                val openURL = Intent(Intent.ACTION_VIEW)
                                                val paypalPaymentMethod = paymentMethodsUserToRefund.value.find { it is PaypalPaymentMethod } as? PaypalPaymentMethod
                                                val paypalUsername = paypalPaymentMethod?.value?.user_paypal
                                                openURL.data = Uri.parse("https://www.paypal.com/paypalme/$paypalUsername")
                                                context.startActivity(openURL)
                                            },
                                            colors = ButtonDefaults.buttonColors(
                                                backgroundColor = Color(android.graphics.Color.parseColor("#00457C")),
                                                contentColor = Color.White
                                            )
                                        ) {
                                            Text(
                                                text = "Rembourser via Paypal"
                                            )
                                        }
                                    }
                                }
                            }
                            if (userHavePaypal && !userHaveRib) {
                                Text(
                                    text = "En cliquant sur \"Rembourser via Paypal\" vous serez redirigé vers le profil de $userToRefundUsername afin d'effectuer le remboursement.",
                                    color = Color.White,
                                    modifier = Modifier.padding(bottom = 16.dp)
                                )
                            }
                        }
                    }
                },
                dismissButton = {
                    Button(
                        onClick = {
                            isRefundPopUpDisplayed.value = false
                        },
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = Color(android.graphics.Color.parseColor("#ffa31a")),
                            contentColor = Color.White
                        )
                    ) {
                        Text("Fermer")
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            CoroutineScope(Dispatchers.Main).launch {
                                val refundDone = RefundRepository(httpClient).makeARefund(jwtToken, groupId, refundToDoDetails.value.id.toString())
                                if (refundDone) {
                                    isRefundPopUpDisplayed.value = false
                                    getGroupInfos()
                                }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = Color.Transparent,
                            contentColor = Color.White
                        ),
                        modifier = Modifier.border(
                            1.dp,
                            Color(android.graphics.Color.parseColor("#FFA31A")),
                            shape = RoundedCornerShape(8.dp)
                        )
                    ) {
                        Text("J'ai effectué le remboursement")
                    }
                },
                backgroundColor = Color(android.graphics.Color.parseColor("#292929")),
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}