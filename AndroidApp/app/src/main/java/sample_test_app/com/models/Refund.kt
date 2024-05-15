package sample_test_app.com.models

import kotlinx.serialization.Serializable

@Serializable
data class Refund(
    val id: String? = "",
    val refundingUserId: String? = "",
    val refundedUserId: String? = "",
    val amount: Double? = 0.0,
    val updatedAt: String? = "",
)