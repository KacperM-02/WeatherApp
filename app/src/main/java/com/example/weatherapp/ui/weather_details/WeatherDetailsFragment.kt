package com.example.weatherapp.ui.weather_details

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
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
            binding.textDashboard.text = weatherData
        }

        val isLoadingObserver = Observer<Boolean> { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE

        }

        val errorObserver = Observer<String> { error ->
            error.let {
                Toast.makeText(context, it, Toast.LENGTH_LONG).show()
            }
        }

        weatherDetailsViewModel.weatherData.observe(viewLifecycleOwner, weatherDataObserver)
        weatherDetailsViewModel.isLoading.observe(viewLifecycleOwner, isLoadingObserver)
        weatherDetailsViewModel.error.observe(viewLifecycleOwner, errorObserver)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}