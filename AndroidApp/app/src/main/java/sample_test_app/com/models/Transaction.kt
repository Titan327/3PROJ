package sample_test_app.com.models

import kotlinx.serialization.Serializable

@Serializable
data class Transaction(
    val id: String?,
    val groupId: String?,
    val label: String?,
    val total_amount: Int?,
    val date: String?,
    val receipt: String?,
    val senderId: String?,
    val categoryId: String?,
    val createdAt: String?,
    val updatedAt: String?,
    val Group: Group?,
)

@Serializable
data class TransactionUser(
    val id: String?,
    val userId: String?,
    val transactionId: String?,
    val amount: Double?,
    val createdAt: String?,
    val updatedAt: String?,
    val Transaction: Transaction?
)