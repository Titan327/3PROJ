package sample_test_app.com.models

import kotlinx.serialization.Serializable

@Serializable
data class Message(
    val _id: String,
    val text: String,
    val userId: Int,
    val groupId: String,
    val timestamp: String
)

@Serializable
data class MessagesContainer(val messages: List<Message>)
