import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

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