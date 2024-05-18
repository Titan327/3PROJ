import android.annotation.SuppressLint
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

@Serializable
data class PaymentCard(val type: String)

@Serializable
data class PaymentMethodData(
    val id: String,
    val type: String,
    val value: PaymentValue,
    val bank_link: String? = null
)

@Serializable
data class PaymentValue(
    val name: String? = null,
    val surname: String? = null,
    val bank_name: String? = null,
    val bank_number: String? = null,
    val box_code: String? = null,
    val account_number: String? = null,
    val RIB_key: String? = null,
    val IBAN: String? = null,
    val user_paypal: String? = null
)

@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun fetchPaymentMethods(httpClient: HttpClient, jwtToken: String): List<PaymentMethodData> {
    val paymentMethods = remember { mutableStateOf(emptyList<PaymentMethodData>()) }

    CoroutineScope(Dispatchers.IO).launch {
        val response: HttpResponse =
            httpClient.get("https://3proj-back.tristan-tourbier.com/api/users/me/paymentMethode") {
                header("Authorization", "Bearer $jwtToken")
            }
        val paymentMethodsResponse =
            Json.decodeFromString<List<PaymentMethodData>>(response.bodyAsText())
        withContext(Dispatchers.Main) {
            paymentMethods.value = paymentMethodsResponse
        }
    }

    return paymentMethods.value
}