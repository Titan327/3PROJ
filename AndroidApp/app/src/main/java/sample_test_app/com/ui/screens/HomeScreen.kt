package sample_test_app.com.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavController
import io.ktor.client.HttpClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import sample_test_app.com.LocalJwtToken
import sample_test_app.com.LocalUser
import sample_test_app.com.http.Repository.GroupRepository
import sample_test_app.com.http.Repository.TransactionRepository
import sample_test_app.com.models.Group
import sample_test_app.com.models.Transaction
import sample_test_app.com.models.TransactionUser


@Composable
fun HomeScreen(httpClient: HttpClient, navController: NavController) {
    val jwtToken = LocalJwtToken.current
    val userId = LocalUser.current.id.toString()
    var groups: List<Group> = emptyList()
    var transactions: List<TransactionUser> = emptyList()
    val groupRepository = GroupRepository(httpClient)
    val transactionRepository = TransactionRepository(httpClient)

    LaunchedEffect(key1 = userId) {
        CoroutineScope(Dispatchers.Main).launch {
            groups = groupRepository.getUserGroups(userId, jwtToken, true)
            transactions = transactionRepository.getLastTransactions(userId, jwtToken)
        }
    }

}