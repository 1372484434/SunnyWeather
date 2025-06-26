package com.example.sunnyweather.logic.dao

import android.content.Context
import androidx.core.content.edit
import com.example.sunnyweather.logic.model.Place
import com.google.gson.Gson
import com.example.sunnyweather.SunnyWeatherApplication

object PlaceDao {

    fun savePlace(place: Place) {
        sharedPreferences().edit().putString("place", Gson().toJson(place)).apply()
    }

    fun getSavedPlace(): Place {
        val placeJson = sharedPreferences().getString("place", null)
        return Gson().fromJson(placeJson ?: "", Place::class.java)
    }

    fun isPlaceSaved() = sharedPreferences().contains("place")

    private fun sharedPreferences() =
        SunnyWeatherApplication.context.getSharedPreferences("sunny_weather", Context.MODE_PRIVATE)

}