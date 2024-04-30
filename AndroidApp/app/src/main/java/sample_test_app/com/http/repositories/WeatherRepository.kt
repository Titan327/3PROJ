package sample_test_app.com.http.repositories

import io.ktor.client.call.*
import io.ktor.client.request.*
import sample_test_app.com.http.AppHttpClient
import sample_test_app.com.http.types.Weather

class WeatherRepository {
    companion object {
        private val client = AppHttpClient.getClient()
        private const val API_URL = "https://api.open-meteo.com/v1"
    }

    suspend fun getWeather(latitude: Double, longitude: Double): Weather {
        val response = client.get("$API_URL/forecast?latitude=$latitude&longitude=$longitude&current_weather=true")
        return response.body()
    }
}