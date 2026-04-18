package org.turtledev.richard.data.api

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.serialization.Serializable

@Serializable
data class Message(
    val id: Int,
    val user: String,
    val text: String,
    val timestamp: Long
)

@Serializable
data class SendMessageBody(val text: String)

@Serializable
data class GetMessagesResponse(
    val messages: List<Message>
)

class ChatApiService(private val client: HttpClient) {

    suspend fun getMessages(serverIp: String, lastId: Int, username: String, password: String): Result<GetMessagesResponse> {
        return runCatching {
            client.get("$serverIp/get") {
                parameter("last_id", lastId)
                header("X-User", username)
                header("X-Pass", password)
            }.body()
        }
    }

    suspend fun sendMessage(serverIp: String, text: String, username: String, password: String): Result<Message> {
        return runCatching {
            client.post("$serverIp/send") {
                header("X-User", username)
                header("X-Pass", password)
                contentType(ContentType.Application.Json)
                setBody(SendMessageBody(text))
            }.body()
        }
    }
}