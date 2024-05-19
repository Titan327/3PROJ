package sample_test_app.com.http.Repository

import android.os.Build
import androidx.annotation.RequiresApi
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.util.InternalAPI
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import org.json.JSONArray
import org.json.JSONObject
import sample_test_app.com.models.Transaction
import sample_test_app.com.models.TransactionUser
import java.time.LocalDate


class TransactionRepository(private val httpClient: HttpClient) {


    suspend fun getLastTransactions(userId: String, jwtToken: String): List<TransactionUser> {
        try {
            val userResponse: HttpResponse = withContext(Dispatchers.IO) {
                httpClient.get("https://3proj-back.tristan-tourbier.com/api/users/${userId}/lastTransactions?limit=3") {
                    contentType(ContentType.Application.Json)
                    header("Authorization", "Bearer $jwtToken")
                }
            }
            return if (userResponse.status == HttpStatusCode.OK) {
                val responseBody = userResponse.body<String>()
                val json = Json { ignoreUnknownKeys = true; isLenient = true }
                return json.decodeFromString<List<TransactionUser>>(responseBody)
            } else {
                println("Error: ${userResponse.status}")
                emptyList()
            }
        } catch (e: Exception) {
            println("Error: $e")
            return emptyList()
        }
    }

    suspend fun getGroupTransactions (groupId: String, jwtToken: String): List<Transaction> {
        try {
            val userResponse: HttpResponse = withContext(Dispatchers.IO) {
                httpClient.get("https://3proj-back.tristan-tourbier.com/api/groups/${groupId}/transactions") {
                    contentType(ContentType.Application.Json)
                    header("Authorization", "Bearer $jwtToken")
                }
            }
            return if (userResponse.status == HttpStatusCode.OK) {
                val responseBody = userResponse.body<String>()
                val json = Json { ignoreUnknownKeys = true; isLenient = true }
                return json.decodeFromString<List<Transaction>>(responseBody)
            } else {
                println("Error: ${userResponse.status}")
                emptyList()
            }
        } catch (e: Exception) {
            println("Error: $e")
            return emptyList()
        }
    }

    suspend fun getRestToPay(userId: String, jwtToken: String): Pair<Float, Int> {
        try {
            val response: HttpResponse = withContext(Dispatchers.IO) {
                httpClient.get("https://3proj-back.tristan-tourbier.com/api/users/${userId}/transactions/totalBalance") {
                    contentType(ContentType.Application.Json)
                    header("Authorization", "Bearer $jwtToken")
                }
            }
            return if (response.status == HttpStatusCode.OK) {
                val responseBody = response.body<String>()
                val jsonObject = JSONObject(responseBody)
                val amount = jsonObject.getInt("amount").toFloat()
                val transactions = jsonObject.getInt("transactions")
                Pair(amount, transactions)
            } else {
                println("Error: ${response.status}")
                Pair(0.0f, 0)
            }
        } catch (e: Exception) {
            println("Error: $e")
            return Pair(0.0f, 0)
        }
    }

    suspend fun getTotalPaidThisMonth(userId: String, jwtToken: String): Pair<Float, Int> {
        try {
            val response: HttpResponse = withContext(Dispatchers.IO) {
                httpClient.get("https://3proj-back.tristan-tourbier.com/api/users/${userId}/transactions/thisMonth") {
                    contentType(ContentType.Application.Json)
                    header("Authorization", "Bearer $jwtToken")
                }
            }
            return if (response.status == HttpStatusCode.OK) {
                val responseBody = response.body<String>()
                val jsonObject = JSONObject(responseBody)
                val amount = jsonObject.getInt("amount").toFloat()
                val transactions = jsonObject.getInt("transactions")
                Pair(amount, transactions)
            } else {
                println("Error: ${response.status}")
                Pair(0.0f, 0)
            }
        } catch (e: Exception) {
            println("Error: $e")
            return Pair(0.0f, 0)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @OptIn(InternalAPI::class)
    suspend fun createTransaction(groupId: Int, jwtToken: String, label: String, total_amount: Float, date: LocalDate, receipt: String, senderId: Int, categoryId: Int, details: List<TransactionUser>): String {
        return try {
            val response: HttpResponse = withContext(Dispatchers.IO) {
                httpClient.post("https://3proj-back.tristan-tourbier.com/api/groups/${groupId}/transactions") {
                    contentType(ContentType.Application.Json)
                    header("Authorization", "Bearer $jwtToken")
                    val detailsJsonArray = JSONArray()
                    for (detail in details) {
                        val detailJsonObject = JSONObject().apply {
                            put("userId", detail.userId)
                            put("amount", detail.amount)
                        }
                        detailsJsonArray.put(detailJsonObject)
                    }
                    body = JSONObject().apply {
                        put("label", label)
                        put("total_amount", total_amount)
                        put("date", date)
                        put("receipt", receipt)
                        put("senderId", senderId)
                        put("categoryId", categoryId)
                        put("details", detailsJsonArray)
                    }.toString()
                }
            }
            if (response.status == HttpStatusCode.OK || response.status == HttpStatusCode.Created) {
                "true"
            } else {
                response.body()
            }
        } catch (e: Exception) {
            println("Error: $e")
            e.toString()
        }
    }
}
