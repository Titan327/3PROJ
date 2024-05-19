package sample_test_app.com.models

import kotlinx.serialization.Serializable

@Serializable
data class Transaction(
    val id: String? = "",
    val groupId: String? = "",
    val label: String? = "",
    val total_amount: Float? = 0.0f,
    val date: String? = "",
    val receipt: String? = "",
    val senderId: String? = "",
    val categoryId: String? = "",
    val createdAt: String? = "",
    val updatedAt: String? = "",
    val Group: Group? = null,
    val TransactionUsers: List<TransactionUser>? = null
)

@Serializable
data class TransactionUser(
    val id: String? = "",
    val userId: String? = "",
    val transactionId: String? = "",
    val amount: Double? = 0.0,
    val createdAt: String? = "",
    val updatedAt: String? = "",
    val Transaction: Transaction? = null,
)