import android.annotation.SuppressLint
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.client.statement.readText
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

@Serializable
data class PaymentCard(val type: String)

@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun fetchPaymentMethods(httpClient: HttpClient, jwtToken: String): List<PaymentMethod> {
    val paymentMethods = remember { mutableStateOf(emptyList<PaymentMethod>()) }

    CoroutineScope(Dispatchers.IO).launch {
        val response: HttpResponse = httpClient.get("https://3proj-back.tristan-tourbier.com/api/users/me/paymentMethode") {
            header("Authorization", "Bearer $jwtToken")
        }
        val paymentMethodsResponse = Json.decodeFromString<List<PaymentMethod>>(response.bodyAsText())
        withContext(Dispatchers.Main) {
            paymentMethods.value = paymentMethodsResponse
        }
    }

    return paymentMethods.value
}