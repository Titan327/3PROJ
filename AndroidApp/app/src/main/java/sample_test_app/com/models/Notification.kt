import kotlinx.serialization.Serializable

@Serializable
data class Notification(
    val _id: String?,
    val user_id: Int?,
    val message: String?,
    val link: String?,
    val seen: Boolean?,
    val date: String?
)