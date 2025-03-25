package com.example.weatherapp.ui.weather_data

import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.example.weatherapp.databinding.FragmentWeatherDataBinding

class WeatherDataFragment : Fragment() {
    private var _binding: FragmentWeatherDataBinding? = null
    private val binding get() = _binding!!
    private val weatherDataViewModel: WeatherDataViewModel by activityViewModels()
    private var currentCity: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupObservers()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWeatherDataBinding.inflate(inflater, container, false)
        val root: View = binding.root
//        setupFavoriteButton()
        return root
    }

    private fun setupObservers() {
        val weatherDataObserver = Observer<String> { weatherData ->
            binding.textHome.text = weatherData
            currentCity = weatherData.split("\n").firstOrNull()?.removePrefix("City: ")?.split(",")?.firstOrNull()?.trim() ?: ""
//            updateFavoriteIcon()
            Log.d("WeatherDataFragment", "setupObservers(): weatherData: $weatherData")
        }

        val weatherIconObserver = Observer<ByteArray> { iconBytes ->
            val bitmap = BitmapFactory.decodeByteArray(iconBytes, 0, iconBytes.size)
            binding.weatherIcon.setImageBitmap(bitmap)
        }

        val isLoadingObserver = Observer<Boolean> { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        val errorObserver = Observer<String> { error ->
            error.let {
                Toast.makeText(context, it, Toast.LENGTH_LONG).show()
            }
        }

        weatherDataViewModel.weatherData.observe(requireActivity(), weatherDataObserver)
        weatherDataViewModel.weatherIcon.observe(requireActivity(), weatherIconObserver)
        weatherDataViewModel.isLoading.observe(requireActivity(), isLoadingObserver)
        weatherDataViewModel.error.observe(requireActivity(), errorObserver)
    }

//    private fun setupFavoriteButton() {
//        binding.favoriteIcon.setOnClickListener {
//            if (currentCity.isNotBlank()) {
//                if (!settingsViewModel.isCityFavorite(currentCity)) {
//                    settingsViewModel.addFavoriteCity(currentCity)
//                }
//                else {
//                    settingsViewModel.removeFavoriteCity(currentCity)
//                }
//                updateFavoriteIcon()
//            }
//        }
//    }
//
//    private fun updateFavoriteIcon() {
//        val isFavorite = settingsViewModel.isCityFavorite(currentCity)
//        binding.favoriteIcon.setImageResource(
//            if (isFavorite) R.drawable.ic_star_active_24 else R.drawable.ic_star_inactive_24
//        )
//    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}