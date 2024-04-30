package sample_test_app.com.ui.screens

import android.util.Log
import androidx.compose.material.Text
import androidx.compose.runtime.*
import kotlinx.coroutines.launch
import sample_test_app.com.http.repositories.WeatherRepository
import sample_test_app.com.http.types.Weather

const val LILLE_LATITUDE = 50.63
const val LILLE_LONGITUDE = 3.06

@Composable
fun WeatherScreen() {
    val scope = rememberCoroutineScope()
    val weather = remember { mutableStateOf<Weather?>(null) }

    LaunchedEffect(true) {
        scope.launch {
            try {
                weather.value = WeatherRepository().getWeather(LILLE_LATITUDE, LILLE_LONGITUDE)
            } catch (e: Exception) {
                Log.e("WeatherScreen", "Error while getting weather", e)
            }
        }
    }

    if (weather.value != null) {
        Text(text = "Weather in Lille: ${weather.value!!.currentWeather.temperature}°C")
    } else {
        Text(text = "Loading weather...")
    }
}
