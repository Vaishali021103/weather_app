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
import android.location.Address
import android.location.Location
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*
import android.location.Geocoder


@Suppress("DEPRECATION")
class MainActivity : AppCompatActivity() {
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var geocoder: Geocoder
    private val LOCATION_PERMISSION_REQUEST_CODE = 1
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        geocoder = Geocoder(this, Locale.getDefault())

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

                    val addresses = geocoder.getFromLocation(latitude, longitude, 1)
                    if (addresses?.isNotEmpty() == true){
                        val addresses = addresses[0]
                        val placeName = addresses.getAddressLine(0)
                        val locn = findViewById<TextView>(R.id.location)
//                        val formattedAddress = formatAddress(addresses)
                        locn.text = placeName

                    }
                    else{
                        val locn = findViewById<TextView>(R.id.location)
                        locn.text = "Place name not found"
                    }
                    // Call the weather API with retrieved coordinates
                    getWeatherData(latitude, longitude)
                }
            }
            .addOnFailureListener {
                Toast.makeText(this,"Failureee",Toast.LENGTH_SHORT).show()
            }
    }

    private fun getWeatherData(latitude: Double, longitude: Double) {
        val apiKey = "62fa0f3f1d8105534a3605af3bf67cae" // Replace with your actual API key
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
                        val decimalFormat = DecimalFormat("#.##")
                        val temp2 = (weatherResponse.main.temp - 273.15)
                        val formattedtemp =decimalFormat.format((temp2))
                        temp.text = formattedtemp.toString()
//                        val humidity = weatherResponse.main.humidity
//                        val weatherDescription = weatherResponse.weather[0].description
                        // Update UI with weather details
                        val date = findViewById<TextView>(R.id.date)
                        val calendar = Calendar.getInstance()
                        val dateFormat = SimpleDateFormat("EEE, MMMM dd, yyyy", Locale.getDefault())
                        val formattedDate = dateFormat.format(calendar.time)
                        date.text = formattedDate.toString()
                    }
                } else {
                    // Handle API error
                    Toast.makeText(this@MainActivity,"Errorrrr",Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<WeatherResponse>, t: Throwable) {
                // Handle network failure or request cancellation
            }
        })
    }
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            LOCATION_PERMISSION_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission granted, perform the operation
                    getLastKnownLocation()
                } else {
                    // Permission denied, handle accordingly (e.g., show a message, disable functionality)
                    // ...
                }
            }
        }
    }


    private fun performOperation() {
        // Place your code here to perform the operation after obtaining permission
        // ...
    }
}