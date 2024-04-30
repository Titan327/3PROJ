package sample_test_app.com.http.types

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Weather (
    @SerialName("latitude") val latitude: Double,
    @SerialName("longitude") val longitude: Double,
    @SerialName("generationtime_ms") val generationTime: Float,
    @SerialName("utc_offset_seconds") val utcOffsetInSeconds: Int,
    @SerialName("timezone") val timezone: String,
    @SerialName("timezone_abbreviation") val timezoneAbbreviation: String,
    @SerialName("elevation") val elevation: Double,
    @SerialName("current_weather") val currentWeather: CurrentWeather,
    @SerialName("current_weather_units") val currentWeatherUnits: CurrentWeatherUnits,
)

@Serializable
data class CurrentWeather (
    @SerialName("is_day") val isDay: Int,
    @SerialName("interval") val interval: Int,
    @SerialName("temperature") val temperature: Double,
    @SerialName("windspeed") val windSpeed: Double,
    @SerialName("winddirection") val windDirection: Double,
    @SerialName("weathercode") val weatherCode: Int,
    @SerialName("time") val time: String,
)

@Serializable
data class CurrentWeatherUnits (
    @SerialName("is_day") val isDay: String,
    @SerialName("interval") val interval: String,
    @SerialName("temperature") val temperature: String,
    @SerialName("windspeed") val windSpeed: String,
    @SerialName("winddirection") val windDirection: String,
    @SerialName("weathercode") val weatherCode: String,
    @SerialName("time") val time: String,
)

