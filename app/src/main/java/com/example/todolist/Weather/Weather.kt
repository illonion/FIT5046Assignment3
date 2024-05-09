import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.todolist.R
import com.example.todolist.Weather.ItemsRepository
import com.example.todolist.Weather.data.models.CurrentWeather
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.math.roundToInt

@Composable
fun WeatherScreen() {
    val repository = remember { ItemsRepository() }
    var currentWeather by remember { mutableStateOf<CurrentWeather?>(null) }

    val coroutineScope = rememberCoroutineScope()


    LaunchedEffect(Unit) {
        coroutineScope.launch {
            val weather = withContext(Dispatchers.IO) {
                repository.getCurrentWeather(
                    -37.814,  // lon 144.96332, lat -37.814  for Melbourne in Australia
                    144.96332,
                    "metric"
                )
            }
            currentWeather = weather
        }
    }

    currentWeather?.let { weather ->
        Box(
            modifier = Modifier
                .size(width = 185.dp, height = 185.dp)
            .background(Color.LightGray),
        )
        {
            when (weather.weather[0].main) {

                "Thunderstorm" -> {
                    Image(
                        painter = painterResource(R.drawable.thunderstorm),
                        contentDescription = null,
                        modifier = Modifier
                            .size(width = 185.dp, height = 185.dp)
                    )
                }

                "Drizzle" -> {
                    Image(
                        painter = painterResource(R.drawable.drizzle),
                        contentDescription = null,
                        modifier = Modifier
                            .size(width = 185.dp, height = 185.dp)
                    )
                }

                "Rain" -> {
                    Image(
                        painter = painterResource(R.drawable.rain),
                        contentDescription = null,
                        modifier = Modifier
                            .size(width = 185.dp, height = 185.dp)
                    )
                }

                "Snow" -> {
                    Image(
                        painter = painterResource(R.drawable.snow),
                        contentDescription = null,
                        modifier = Modifier
                            .size(width = 185.dp, height = 185.dp)
                    )
                }

                "Clear" -> {
                    Image(
                        painter = painterResource(R.drawable.clear),
                        contentDescription = null,
                        modifier = Modifier
                            .size(width = 185.dp, height = 185.dp)
                    )
                }

                "Clouds" -> {
                    Image(
                        painter = painterResource(R.drawable.clouds),
                        contentDescription = null,
                        modifier = Modifier
                            .size(width = 185.dp, height = 185.dp)
                    )
                }

                else -> {
                    Image(
                        painter = painterResource(R.drawable.mist),
                        contentDescription = null,
                        modifier = Modifier
                            .size(width = 185.dp, height = 185.dp)
                    )
                }
            }

            Text(
                weather.weather[0].main,
                modifier = Modifier.align(Alignment.TopCenter)
                    .padding(top = 20.dp),
                color = Color.White,
                fontSize = 30.sp,
            )
            Text(
                "${weather.main.temp.roundToInt()}°C",
                modifier = Modifier.align(Alignment.Center),
                color = Color.White,
                fontSize = 80.sp
            )
        }
    }
}
