package com.example.weatherapp.ui.weather_data

import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.weatherapp.R
import com.example.weatherapp.databinding.FragmentWeatherDataBinding
import com.example.weatherapp.ui.settings.SettingsViewModel

class WeatherDataFragment : Fragment() {
    private var _binding: FragmentWeatherDataBinding? = null
    private val binding get() = _binding!!
    private lateinit var weatherDataViewModel: WeatherDataViewModel
    private lateinit var settingsViewModel: SettingsViewModel
    private var currentCity: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        weatherDataViewModel = ViewModelProvider(this)[WeatherDataViewModel::class.java]
        settingsViewModel = ViewModelProvider(requireActivity())[SettingsViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWeatherDataBinding.inflate(inflater, container, false)
        val root: View = binding.root

        setupObservers()
        setupFavoriteButton()

        return root
    }

    private fun setupObservers() {
        weatherDataViewModel.weatherData.observe(viewLifecycleOwner) { weatherData ->
            binding.textHome.text = weatherData
            currentCity = weatherData.split("\n").firstOrNull()?.removePrefix("City: ")?.split(",")?.firstOrNull()?.trim() ?: ""
            updateFavoriteIcon()
        }

        weatherDataViewModel.weatherIcon.observe(viewLifecycleOwner) { iconBytes ->
            val bitmap = BitmapFactory.decodeByteArray(iconBytes, 0, iconBytes.size)
            binding.weatherIcon.setImageBitmap(bitmap)
        }

        weatherDataViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        weatherDataViewModel.error.observe(viewLifecycleOwner) { error ->
            error?.let {
                Toast.makeText(context, it, Toast.LENGTH_LONG).show()
            }
        }

        Log.d("WeatherDataFragment", "setupObservers(): called")
        settingsViewModel.chosenCityId.observe(viewLifecycleOwner) { cityId ->
            weatherDataViewModel.fetchWeatherData(cityId)
            Log.d("WeatherDataFragment", "setupObservers(): cityID: $cityId")
        }
    }

    private fun setupFavoriteButton() {
        binding.favoriteIcon.setOnClickListener {
            if (currentCity.isNotBlank()) {
                if (!settingsViewModel.isCityFavorite(currentCity)) {
                    settingsViewModel.addFavoriteCity(currentCity)
                }
                else {
                    settingsViewModel.removeFavoriteCity(currentCity)
                }
                updateFavoriteIcon()
            }
        }
    }

    private fun updateFavoriteIcon() {
        val isFavorite = settingsViewModel.isCityFavorite(currentCity)
        binding.favoriteIcon.setImageResource(
            if (isFavorite) R.drawable.ic_star_active_24 else R.drawable.ic_star_inactive_24
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}