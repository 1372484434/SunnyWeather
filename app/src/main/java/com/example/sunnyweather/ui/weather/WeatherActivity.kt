package com.example.sunnyweather.ui.weather

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ViewModelProviders
import androidx.lifecycle.lifecycleScope
import com.example.sunnyweather.R
import com.example.sunnyweather.databinding.ActivityWeatherBinding
import com.example.sunnyweather.databinding.ForecastBinding
import com.example.sunnyweather.databinding.LifeIndexBinding
import com.example.sunnyweather.databinding.NowBinding
import com.example.sunnyweather.logic.model.Weather
import com.example.sunnyweather.logic.model.getSky
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class WeatherActivity : AppCompatActivity() {

    private lateinit var binding: ActivityWeatherBinding
//    private lateinit var nowBinding: NowBinding
//    private lateinit var forecastBinding: ForecastBinding
//    private lateinit var lifeIndexBinding: LifeIndexBinding

    private val viewModel: WeatherViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 设置透明状态栏
        if (Build.VERSION.SDK_INT >= 21) {
            window.decorView.systemUiVisibility =
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            window.statusBarColor = Color.TRANSPARENT
        }

        // 初始化 ViewBinding
        binding = ActivityWeatherBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 绑定子布局
//        nowBinding = NowBinding.bind(binding.nowLayout)
//        forecastBinding = ForecastBinding.bind(binding.forecastLayout)
//        lifeIndexBinding = LifeIndexBinding.bind(binding.lifeIndexLayout)

        // 初始化 ViewModel 数据
        intent.getStringExtra("location_lng")?.let { viewModel.locationLng = it }
        intent.getStringExtra("location_lat")?.let { viewModel.locationLat = it }
        intent.getStringExtra("place_name")?.let { viewModel.placeName = it }

        // 监听数据
        viewModel.weatherLiveData.observe(this) { result ->
            val weather = result.getOrNull()
            if (weather != null) {
                showWeatherInfo(weather)
            } else {
                Toast.makeText(this, "无法成功获取天气信息", Toast.LENGTH_SHORT).show()
                result.exceptionOrNull()?.printStackTrace()
            }
//            binding.swipeRefresh.isRefreshing = false
        }

//        // 下拉刷新
//        binding.swipeRefresh.setColorSchemeResources(R.color.colorPrimary)
//        binding.swipeRefresh.setOnRefreshListener {
//            refreshWeather()
//        }
//
//        // Drawer
//        binding.navBtn.setOnClickListener {
//            binding.drawerLayout.openDrawer(GravityCompat.START)
//        }
//        binding.drawerLayout.addDrawerListener(object : androidx.drawerlayout.widget.DrawerLayout.DrawerListener {
//            override fun onDrawerStateChanged(newState: Int) {}
//            override fun onDrawerSlide(drawerView: View, slideOffset: Float) {}
//            override fun onDrawerOpened(drawerView: View) {}
//            override fun onDrawerClosed(drawerView: View) {
//                val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
//                imm.hideSoftInputFromWindow(drawerView.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
//            }
//        })

        // 首次刷新
        refreshWeather()
    }

    private fun refreshWeather() {
        viewModel.refreshWeather(viewModel.locationLng, viewModel.locationLat)
//        binding.swipeRefresh.isRefreshing = true
    }

    private fun showWeatherInfo(weather: Weather) {
        val realtime = weather.realtime
        val daily = weather.daily

        // now.xml
        binding.nowLayout.placeName.text = viewModel.placeName
        binding.nowLayout.currentTemp.text = "${realtime.temperature.toInt()} ℃"
        binding.nowLayout.currentSky.text = getSky(realtime.skycon).info
        binding.nowLayout.currentAQI.text = "空气指数 ${realtime.airQuality.aqi.chn.toInt()}"
        binding.nowLayout.nowLayout.setBackgroundResource(getSky(realtime.skycon).bg)

        // forecast.xml
        binding.forecastLayout.forecastLayout.removeAllViews()
        val inflater = LayoutInflater.from(this)
        val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        for (i in daily.skycon.indices) {
            val skycon = daily.skycon[i]
            val temperature = daily.temperature[i]
            val view = inflater.inflate(R.layout.forecast_item, binding.forecastLayout.forecastLayout, false)
            view.findViewById<TextView>(R.id.dateInfo).text = simpleDateFormat.format(skycon.date)
            val sky = getSky(skycon.value)
            view.findViewById<ImageView>(R.id.skyIcon).setImageResource(sky.icon)
            view.findViewById<TextView>(R.id.skyInfo).text = sky.info
            view.findViewById<TextView>(R.id.temperatureInfo).text =
                "${temperature.min.toInt()} ~ ${temperature.max.toInt()} ℃"
            binding.forecastLayout.forecastLayout.addView(view)
        }

        // life_index.xml
        binding.lifeIndexLayout.coldRiskText.text = daily.lifeIndex.coldRisk[0].desc
        binding.lifeIndexLayout.dressingText.text = daily.lifeIndex.dressing[0].desc
        binding.lifeIndexLayout.ultravioletText.text = daily.lifeIndex.ultraviolet[0].desc
        binding.lifeIndexLayout.carWashingText.text = daily.lifeIndex.carWashing[0].desc

        binding.weatherLayout.visibility = View.VISIBLE

        val temp = realtime.temperature.toInt()
        val suggestion = when {
            temp >= 28 -> "建议穿短袖，注意防晒。"
            temp in 20..27 -> "可以穿薄外套或长袖衬衫。"
            temp in 10..19 -> "建议穿毛衣或夹克。"
            temp in 0..9 -> "请穿棉衣，注意保暖。"
            else -> "气温较低，建议穿羽绒服。"
        }
        binding.nowLayout.dressingSuggestion.text = suggestion

    }
}


//class WeatherActivity : AppCompatActivity() {
//
//    private lateinit var binding: ActivityWeatherBinding
//
//    private val viewModel: WeatherViewModel by viewModels()
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//
//        if (Build.VERSION.SDK_INT >= 21) {
//            val decorView = window.decorView
//            decorView.systemUiVisibility =
//                View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
//            window.statusBarColor = Color.TRANSPARENT
//        }
//
//        binding = ActivityWeatherBinding.inflate(layoutInflater)
//        setContentView(binding.root)
//
//        initViewModelData()
//        setupObservers()
//        //setupSwipeRefresh()
//        //setupDrawer()
//    }
//
//    private fun initViewModelData() {
//        intent.getStringExtra("location_lng")?.let { viewModel.locationLng = it }
//        intent.getStringExtra("location_lat")?.let { viewModel.locationLat = it }
//        intent.getStringExtra("place_name")?.let { viewModel.placeName = it }
//        refreshWeather()
//    }
//
//    private fun setupObservers() {
//        viewModel.weatherLiveData.observe(this) { result ->
//            val weather = result.getOrNull()
//            if (weather != null) {
//                showWeatherInfo(weather)
//            } else {
//                Toast.makeText(this, "无法成功获取天气信息", Toast.LENGTH_SHORT).show()
//                result.exceptionOrNull()?.printStackTrace()
//            }
//            binding.swipeRefresh.isRefreshing = false
//        }
//    }
//
//    private fun setupSwipeRefresh() {
//        binding.swipeRefresh.setColorSchemeResources(com.example.sunnyweather.R.color.colorPrimary)
//        binding.swipeRefresh.setOnRefreshListener { refreshWeather() }
//    }
//
////    private fun setupDrawer() {
////        binding.navBtn.setOnClickListener {
////            binding.drawerLayout.openDrawer(GravityCompat.START)
////        }
////
////        binding.drawerLayout.addDrawerListener(object : DrawerLayout.DrawerListener {
////            override fun onDrawerStateChanged(newState: Int) {}
////            override fun onDrawerSlide(drawerView: View, slideOffset: Float) {}
////            override fun onDrawerOpened(drawerView: View) {}
////            override fun onDrawerClosed(drawerView: View) {
////                val imm = getSystemService(InputMethodManager::class.java)
////                imm?.hideSoftInputFromWindow(
////                    drawerView.windowToken,
////                    InputMethodManager.HIDE_NOT_ALWAYS
////                )
////            }
////        })
////    }
//
//    private fun refreshWeather() {
//        viewModel.refreshWeather(viewModel.locationLng, viewModel.locationLat)
//        binding.swipeRefresh.isRefreshing = true
//    }
//
//    private fun showWeatherInfo(weather: Weather) {
//        val realtime = weather.realtime
//        val daily = weather.daily
//
//        // now.xml
//        binding.nowLayout.placeName.text = viewModel.placeName
//        binding.nowLayout.currentTemp.text = "${realtime.temperature.toInt()} ℃"
//        binding.nowLayout.currentSky.text = getSky(realtime.skycon).info
//        binding.nowLayout.currentAQI.text = "空气指数 ${realtime.airQuality.aqi.chn.toInt()}"
//        binding.nowLayout.nowLayout.setBackgroundResource(getSky(realtime.skycon).bg)
//
//        // forecast.xml
//        binding.forecastLayout.forecastLayout.removeAllViews()
//        val days = daily.skycon.size
//        val inflater = LayoutInflater.from(this)
//        val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
//
//        for (i in 0 until days) {
//            val skycon = daily.skycon[i]
//            val temperature = daily.temperature[i]
//            val view = inflater.inflate(R.layout.forecast_item, binding.forecastLayout.forecastLayout, false)
//            view.findViewById<TextView>(com.example.sunnyweather.R.id.dateInfo).text = simpleDateFormat.format(skycon.date)
//            val sky = getSky(skycon.value)
//            view.findViewById<ImageView>(com.example.sunnyweather.R.id.skyIcon).setImageResource(sky.icon)
//            view.findViewById<TextView>(com.example.sunnyweather.R.id.skyInfo).text = sky.info
//            view.findViewById<TextView>(com.example.sunnyweather.R.id.temperatureInfo).text =
//                "${temperature.min.toInt()} ~ ${temperature.max.toInt()} ℃"
//            binding.forecastLayout.forecastLayout.addView(view)
//        }
//
//        // life_index.xml
//        val lifeIndex = daily.lifeIndex
//        binding.lifeIndexLayout.coldRiskText.text = lifeIndex.coldRisk[0].desc
//        binding.lifeIndexLayout.dressingText.text = lifeIndex.dressing[0].desc
//        binding.lifeIndexLayout.ultravioletText.text = lifeIndex.ultraviolet[0].desc
//        binding.lifeIndexLayout.carWashingText.text = lifeIndex.carWashing[0].desc
//
//        binding.weatherLayout.visibility = View.VISIBLE
//    }
//}
