package com.example.weatherapp.ui.weather_data

import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.example.weatherapp.R
import com.example.weatherapp.data.preferences.WeatherSettingsPreferences
import com.example.weatherapp.databinding.FragmentWeatherDataBinding

class WeatherDataFragment : Fragment() {
    private var _binding: FragmentWeatherDataBinding? = null
    private val binding get() = _binding!!
    private val weatherDataViewModel: WeatherDataViewModel by activityViewModels()
    private lateinit var weatherSettingsPreferences : WeatherSettingsPreferences
    private var currentCity = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWeatherDataBinding.inflate(inflater, container, false)
        weatherSettingsPreferences = WeatherSettingsPreferences(requireContext())
        setupObservers()
        setupFavoriteButton()
        return binding.root
    }

    private fun setupObservers() {
        val weatherDataObserver = Observer<String> { weatherData ->
            binding.textWeatherData.text = weatherData
            currentCity = weatherData.split("\n").firstOrNull()?.removePrefix("City: ")?.split(",")?.firstOrNull()?.trim() ?: ""
            updateFavoriteIcon()
        }

        val weatherIconObserver = Observer<ByteArray> { iconBytes ->
            val bitmap = BitmapFactory.decodeByteArray(iconBytes, 0, iconBytes.size)
            binding.weatherIcon.setImageBitmap(bitmap)
        }

        val isLoadingObserver = Observer<Boolean> { isLoading ->
            if (isLoading) {
                binding.progressBar.visibility = View.VISIBLE
                binding.textWeatherData.visibility = View.GONE
                binding.weatherIcon.visibility = View.GONE
            } else {
                binding.progressBar.visibility = View.GONE
                binding.textWeatherData.visibility = View.VISIBLE
                binding.weatherIcon.visibility = View.VISIBLE
            }
        }

        val errorObserver = Observer<String> { error ->
            if(error.isNotEmpty()) binding.favoriteIcon.visibility = View.GONE
            else binding.favoriteIcon.visibility = View.VISIBLE
        }

        weatherDataViewModel.weatherData.observe(viewLifecycleOwner, weatherDataObserver)
        weatherDataViewModel.weatherIcon.observe(viewLifecycleOwner, weatherIconObserver)
        weatherDataViewModel.isLoading.observe(viewLifecycleOwner, isLoadingObserver)
        weatherDataViewModel.error.observe(viewLifecycleOwner, errorObserver)
    }

    private fun setupFavoriteButton() {
        binding.favoriteIcon.setOnClickListener {
            if (!weatherSettingsPreferences.isCityFavorite(currentCity)) {
                weatherSettingsPreferences.addFavoriteCity(currentCity)
            }
            else {
                weatherSettingsPreferences.removeFavoriteCity(currentCity)
            }
            updateFavoriteIcon()
        }
    }

    private fun updateFavoriteIcon() {
        val isFavorite = weatherSettingsPreferences.isCityFavorite(currentCity)
        binding.favoriteIcon.setImageResource(
            if (isFavorite) R.drawable.ic_star_active_24 else R.drawable.ic_star_inactive_24
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}