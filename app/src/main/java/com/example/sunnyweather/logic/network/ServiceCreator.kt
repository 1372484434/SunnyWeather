package com.example.sunnyweather.logic.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ServiceCreator {

    private const val BASE_URL = "https://api.caiyunapp.com/"

    // ✅ 公开 retrofit，允许 inline 函数访问
    val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    // ✅ Kotlin 推荐写法
    inline fun <reified T> create(): T = retrofit.create(T::class.java)
}
