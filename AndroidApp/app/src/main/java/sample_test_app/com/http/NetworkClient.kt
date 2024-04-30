package sample_test_app.com.http

import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.client.statement.*
import io.ktor.client.utils.EmptyContent.contentType
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.*
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.runBlocking

data class User(val username: String, val password: String)

abstract class AppHttpClient {
    companion object {
        private var client: HttpClient? = null

        @Synchronized
        fun getClient(): HttpClient {
            if (client == null) {
                client = HttpClient(CIO) {
                    install(ContentNegotiation) {
                        json(Json {
                            prettyPrint = true
                            isLenient = true
                        })
                    }
                }
            }

            return client!!
        }
    }
}