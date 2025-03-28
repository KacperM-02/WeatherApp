package com.example.weatherapp.ui.weather_details

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.example.weatherapp.databinding.FragmentWeatherDetailsBinding

class WeatherDetailsFragment : Fragment() {
    private var _binding: FragmentWeatherDetailsBinding? = null
    private val binding get() = _binding!!
    private val weatherDetailsViewModel: WeatherDetailsViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWeatherDetailsBinding.inflate(inflater, container, false)
        setupObservers()
        return binding.root
    }

    private fun setupObservers() {
        val weatherDataObserver = Observer<String> { weatherData ->
            binding.textWeatherDetails.text = weatherData
        }

        val isLoadingObserver = Observer<Boolean> { isLoading ->
            if(isLoading)
            {
                binding.progressBar.visibility = View.VISIBLE
                binding.textWeatherDetails.visibility = View.GONE
            }
            else{
                binding.progressBar.visibility = View.GONE
                binding.textWeatherDetails.visibility = View.VISIBLE
            }
        }

        weatherDetailsViewModel.weatherData.observe(viewLifecycleOwner, weatherDataObserver)
        weatherDetailsViewModel.isLoading.observe(viewLifecycleOwner, isLoadingObserver)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}