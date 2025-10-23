package com.example.weatherapp

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.SearchView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.weatherapp.databinding.ActivityMainBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.Date
import java.text.SimpleDateFormat
import java.util.Locale

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        fetchWeatherData("kolkata")
        searchCity()
    }
    private fun searchCity() {
        val searchView = binding.searchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextChange(query: String?): Boolean {
                return true
            }

            override fun onQueryTextSubmit(query: String?): Boolean {
                if(query != null){
                fetchWeatherData(query)
                }
                return true
            }
        })

            }






    private fun fetchWeatherData(cityName : String) {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.openweathermap.org/data/2.5/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiInterface::class.java)


        val response =
            retrofit.getWeatherData(cityName, "1a9dd7511fb0692a15a9f7afa28d9350", "metric")



        response.enqueue(object : Callback<WeatherData> {
            override fun onResponse(call: Call<WeatherData?>, response: Response<WeatherData?>
            ) {
                val responseBody = response.body()
                if(response.isSuccessful && responseBody != null){
                    val temperature = responseBody.main.temp.toString()
                    val humidity = responseBody.main.humidity
                    val windSpeed = responseBody.wind.speed
                    val sunRise = responseBody.sys.sunrise.toLong()
                    val sunSet = responseBody.sys.sunset.toLong()
                    val seaLevel = responseBody.main.sea_level
                    val condition = responseBody.weather.firstOrNull()?.main?: "unknown"
                    val maxTemp = responseBody.main.temp_max
                    val minTemp = responseBody.main.temp_min






                    binding.temp.text = "$temperature °C"
                    binding.weather.text = condition
                    binding.maxTemp.text = "Max : $maxTemp °C"
                    binding.minTemp.text = "Min : $minTemp °C"
                    binding.humidity.text = "$humidity %"
                    binding.wind.text = "$windSpeed m/s"
                    binding.sunrise.text = "${time(sunRise)} am"
                    binding.sunset.text =  "${time(sunSet) } pm"
                    binding.sea.text = "$seaLevel hpa"
                    binding.condition.text = condition
                    binding.cityName.text = "$cityName"
                    binding.date.text = date()
                    binding.day.text = dayName(System.currentTimeMillis())

                    // Determine if it's day or night
                    val currentTime = System.currentTimeMillis() / 1000
                    val isNight = currentTime < sunRise || currentTime > sunSet

                    changeImagesAccordingToWeatherCondition(condition, isNight)






//                    Log.d("TAG","onResponse: $temperature")
                }
            }



            override fun onFailure(call: Call<WeatherData?>, t: Throwable) {
                Log.d("MainActivity", "onFailure: " + t.message)
            }

        })

    }
    private fun changeImagesAccordingToWeatherCondition(condition: String, isNight: Boolean) {
        when(condition){
            "Clear Sky", "Sunny", "Clear" ->{
                if (isNight) {
                    binding.root.setBackgroundResource(R.drawable.night_clear_background)
                    binding.lottieAnimationView.setAnimation(R.raw.night)
                    changeTextColor(R.color.textColor_night)
                } else {
                    binding.root.setBackgroundResource(R.drawable.sunny_background)
                    binding.lottieAnimationView.setAnimation(R.raw.sun)
                    changeTextColor(R.color.textColor_day)
                }
            }
            "Partly Clouds", "Clouds", "Overcast", "Mist", "Foggy","Haze" ->{
                if (isNight) {
                    binding.root.setBackgroundResource(R.drawable.night_cloud_background)
                    binding.lottieAnimationView.setAnimation(R.raw.night_cloud)
                    changeTextColor(R.color.textColor_night)
                } else {
                    binding.root.setBackgroundResource(R.drawable.cloud_background)
                    binding.lottieAnimationView.setAnimation(R.raw.cloud)
                    changeTextColor(R.color.textColor_cloud)
                }

            }
            "Light Rain", "Drizzle","Rain", "Moderate Rain", "Showers", "Heavy Rain" ->{
                if (isNight) {
                    binding.root.setBackgroundResource(R.drawable.night_rain_background)
                    binding.lottieAnimationView.setAnimation(R.raw.night_rain)
                    changeTextColor(R.color.textColor_night)
                } else {
                    binding.root.setBackgroundResource(R.drawable.rain_background)
                    binding.lottieAnimationView.setAnimation(R.raw.rain)
                    changeTextColor(R.color.textColor_rain)
                }
            }
            "Light Snow","Snow", "Moderate Snow", "Heavy Snow", "Blizzard" ->{
                if(isNight){
                binding.root.setBackgroundResource(R.drawable.snow_background)
                binding.lottieAnimationView.setAnimation(R.raw.snow)
                changeTextColor(R.color.textColor_snow)
                }else{
                    binding.root.setBackgroundResource(R.drawable.snow_background)
                    binding.lottieAnimationView.setAnimation(R.raw.snow)
                    changeTextColor(R.color.textColor_snow)
                }

            }
            "Thunderstorm", "Thundery Showers", "Lightning", "Storm" -> {
                if(isNight) {
                    binding.root.setBackgroundResource(R.drawable.storm_background)
                    binding.lottieAnimationView.setAnimation(R.raw.cloud)
                    changeTextColor(R.color.textColor_night)
                }
                else{
                    binding.root.setBackgroundResource(R.drawable.storm_background)
                    binding.lottieAnimationView.setAnimation(R.raw.cloud)
                    changeTextColor(R.color.textColor_night)
                }
            }
            "Smoke", "Dust", "Sand", "Ash" -> {
                if (isNight) {
                    binding.root.setBackgroundResource(R.drawable.haze_background)
                    binding.lottieAnimationView.setAnimation(R.raw.cloud)
                    changeTextColor(R.color.textColor_night)
                }else{
                    binding.root.setBackgroundResource(R.drawable.haze_background)
                    binding.lottieAnimationView.setAnimation(R.raw.cloud)
                    changeTextColor(R.color.textColor_night)
                }
            }
            "Tornado", "Squall" -> {
                if(isNight) {
                    binding.root.setBackgroundResource(R.drawable.storm_background)
                    binding.lottieAnimationView.setAnimation(R.raw.cloud)
                    changeTextColor(R.color.textColor_night)
                }
                else{
                    binding.root.setBackgroundResource(R.drawable.storm_background)
                    binding.lottieAnimationView.setAnimation(R.raw.cloud)
                    changeTextColor(R.color.textColor_night)
                }
            }
            else ->{
                if (isNight) {
                    binding.root.setBackgroundResource(R.drawable.night_default_background)
                    binding.lottieAnimationView.setAnimation(R.raw.night_cloud)
                    changeTextColor(R.color.textColor_night)
                } else {
                    binding.root.setBackgroundResource(R.drawable.sunny_background)
                    binding.lottieAnimationView.setAnimation(R.raw.sun)
                    changeTextColor(R.color.textColor_day)
                }
            }


        }
        binding.lottieAnimationView.playAnimation()

    }
    private fun changeTextColor(colorResId: Int) {
        val color = ContextCompat.getColor(this, colorResId)
        binding.temp.setTextColor(color)
        binding.weather.setTextColor(color)
        binding.maxTemp.setTextColor(color)
        binding.minTemp.setTextColor(color)
        binding.humidity.setTextColor(color)
        binding.wind.setTextColor(color)
        binding.sunrise.setTextColor(color)
        binding.sunset.setTextColor(color)
        binding.sea.setTextColor(color)
        binding.condition.setTextColor(color)
        binding.cityName.setTextColor(color)
        binding.date.setTextColor(color)
        binding.day.setTextColor(color)
        binding.today.setTextColor(color)
    }

    private fun date() : String{
        val sdf = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
        return sdf.format((Date()))
    }
    private fun dayName(timestamp: Long) : String{
        val sdf = SimpleDateFormat("EEEE", Locale.getDefault())
        return sdf.format((Date()))
    }

    private fun time(timestamp: Long) : String{
        val sdf = SimpleDateFormat("HH : mm a", Locale.getDefault())
        return sdf.format((Date(timestamp*1000)))
    }

}