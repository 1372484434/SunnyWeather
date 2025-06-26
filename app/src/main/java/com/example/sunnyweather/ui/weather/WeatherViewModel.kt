package com.example.sunnyweather.ui.weather

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.switchMap
import com.example.sunnyweather.logic.Repository
import com.example.sunnyweather.logic.model.Location

class WeatherViewModel : ViewModel() {
    private val locationLivewData = MutableLiveData<Location>()

    var locationLng = ""

    var locationLat = ""

    var placeName = ""

    val weatherLiveData = locationLivewData.switchMap { location ->
        liveData {
            emitSource(Repository.refreshWeather(location.lng, location.lat))
        }
    }
    fun refreshWeather(lng: String, lat: String) {
        locationLivewData.value = Location(lng, lat)
    }
}
