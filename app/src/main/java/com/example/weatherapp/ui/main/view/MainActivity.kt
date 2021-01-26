package com.example.weatherapp.ui.main.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.viewpager.widget.ViewPager
import androidx.viewpager2.widget.ViewPager2
import com.example.weatherapp.R
import com.example.weatherapp.data.api.RetrofitBuilder
import com.example.weatherapp.data.api.WeatherApiHelper
import com.example.weatherapp.data.model.City
import com.example.weatherapp.ui.base.ViewModelFactory
import com.example.weatherapp.ui.main.adapter.ViewPagerAdapter
import com.example.weatherapp.ui.main.viewmodel.MainViewModel
import com.example.weatherapp.ui.main.viewmodel.MainViewModel.Companion.CITYID_LIST
import com.example.weatherapp.ui.main.viewmodel.MainViewModel.Companion.CITYID_MELBOURNE
import com.example.weatherapp.utils.Status

class MainActivity : AppCompatActivity() {

	private lateinit var viewPager: ViewPager
	private lateinit var mViewPagerAdapter: ViewPagerAdapter

	private val Any.TAG: String
		get() {
			val tag = javaClass.simpleName
			return if (tag.length <= 23) tag else tag.substring(0, 23)
		}

	private lateinit var viewModel: MainViewModel

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_main)

		setupViewModel()
		setupObservers()
	}

	private fun setupViewModel() {
		viewModel = ViewModelProviders.of(
            this,
            ViewModelFactory(WeatherApiHelper(RetrofitBuilder.weatherApiService))
        ).get(MainViewModel::class.java)
	}

	private fun setupObservers() {
		viewModel.getCurrentWeatherById().observe(this, Observer {
            it?.let { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {
                        resource.data?.let { city -> test(city) }
                    }
                    Status.ERROR -> {
                        Log.e(TAG, " ${it.message}")
                        Toast.makeText(this, it.message, Toast.LENGTH_LONG).show()
                    }
                    Status.LOADING -> {
                    }
                }
            }
        })

		viewModel.getCurrentWeatherByIds(CITYID_LIST).observe(this, Observer {
			it?.let { resource ->
				when (resource.status) {
					Status.SUCCESS -> {
						resource.data?.let { city -> initViewPager(city) }
					}
					Status.ERROR -> {
						Log.e(TAG, " ${it.message}")
						Toast.makeText(this, it.message, Toast.LENGTH_LONG).show()
					}
					Status.LOADING -> {
					}
				}
			}
		})
	}

	private fun initViewPager(cities: MutableList<City>) {
		viewPager = findViewById(R.id.pager)
		mViewPagerAdapter = ViewPagerAdapter(this, cities as ArrayList<City>)
		viewPager.adapter = mViewPagerAdapter
	}

	private fun test(city: City) {
		Log.e("MainActivity", "Retrieved test: ${city.name}")
	}
}