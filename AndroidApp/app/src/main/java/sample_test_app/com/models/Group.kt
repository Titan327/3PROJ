package sample_test_app.com.models

import kotlinx.serialization.Serializable

@Serializable
data class Group(
    val id: String? = "",
    val name: String? = "",
    val description: String? = "",
    val picture: List<String?>? = emptyList(),
    val ownerId: String? = "",
    val createdAt: String? = "",
    val updatedAt: String? = "",
    val activeUsersCount: Int? = 0,
    val isFavorite: Boolean? = false,
    val Users: List<User> = emptyList(),
)