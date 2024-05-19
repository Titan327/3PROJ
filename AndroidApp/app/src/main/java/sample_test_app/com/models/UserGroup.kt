package sample_test_app.com.models

import kotlinx.serialization.Serializable

@Serializable
data class UserGroup(
    val id: String? = "",
    val userId: String? = "",
    val groupId: String? = "",
    val balance: Float? = 0F,
    val favorite: Boolean? = false,
    val active: Boolean? = true,
    val createdAt: String? = "",
    val updatedAt: String? = ""
)