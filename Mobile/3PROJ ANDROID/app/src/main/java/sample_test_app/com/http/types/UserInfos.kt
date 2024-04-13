package sample_test_app.com.http.types

import kotlinx.serialization.Serializable

@Serializable
data class UserInfos(
    val firstname: String,
    val lastname: String,
    val username: String,
    val email: String,
    val birth_date: String,
    val password: String
)


