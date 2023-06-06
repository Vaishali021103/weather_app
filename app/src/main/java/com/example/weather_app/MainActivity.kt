package com.example.weather_app

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.viewpager2.widget.ViewPager2
import com.example.weather_app.adapters.ViewPagerAdapter
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Location
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.logging.Logger


class MainActivity : AppCompatActivity() {
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var in_degree:TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        in_degree=findViewById(R.id.indegree)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        // Check location permission
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            getLastKnownLocation()
        } else {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION), 1)
        }


        val tabLayout = findViewById<TabLayout>(R.id.tab_layout)
        val viewPager2 = findViewById<ViewPager2>(R.id.view_pager2)

        val adapter = ViewPagerAdapter(supportFragmentManager, lifecycle)

        viewPager2.adapter = adapter

        TabLayoutMediator(tabLayout, viewPager2){ tab, position ->
            when(position){
                0->{
                    tab.text = "Today"
                }
                1->{
                    tab.text = "Tomorrow"
                }
                2->{
                    tab.text = "Next 5 days"
                }
            }
        }.attach()
    }
    @SuppressLint("MissingPermission")
    private fun getLastKnownLocation() {
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                location?.let {
                    val latitude = location.latitude
                    val longitude = location.longitude
                    // Call the weather API with retrieved coordinates
                    Log.i("latitude",latitude.toString())
                    Log.i("longitude",longitude.toString())
                    getWeatherData(latitude, longitude)
                }
            }
            .addOnFailureListener {
                Toast.makeText(this,"Failureee",Toast.LENGTH_SHORT).show()
            }
    }

    private fun getWeatherData(latitude: Double, longitude: Double) {
        val apiKey = "447c74d353ece1818c9a34f575fc5138" // Replace with your actual API key
        val weatherApiClient = WeatherApiClient()
        val weatherService = weatherApiClient.getWeatherService()

        val call = weatherService.getWeather(latitude, longitude, apiKey)
        call.enqueue(object : Callback<WeatherResponse> {
            override fun onResponse(call: Call<WeatherResponse>, response: Response<WeatherResponse>) {
                if (response.isSuccessful) {
                    val weatherResponse = response.body()
                    // Handle the weather data and update your UI
                    weatherResponse?.let {
                        val temp = findViewById<TextView>(R.id.indegree)
                        temp.text = weatherResponse.main.temp.toString()
                        val humidity = weatherResponse.main.humidity
                        val weatherDescription = weatherResponse.weather[0].description
                        print("temp $temp")
                        Log.i("Taggg",temp.toString())
                        Log.i("weatherResponse",weatherResponse.toString())
                        // Update UI with weather details

                    }
                } else {
                    // Handle API error
                    Toast.makeText(this@MainActivity,"Error",Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<WeatherResponse>, t: Throwable) {
                // Handle network failure or request cancellation
            }
        })
    }
}