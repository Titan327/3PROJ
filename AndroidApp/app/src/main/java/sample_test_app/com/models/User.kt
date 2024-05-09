package sample_test_app.com.models

import kotlinx.serialization.Serializable

@Serializable
data class User(
    val id: String? = "",
    val firstname: String? = "",
    val lastname: String? = "",
    val username: String? = "",
    val email: String? = "",
    val birth_date: String? = "",
    val profile_picture: List<String?>? = emptyList(),
    val UserGroup : UserGroup = UserGroup()
)