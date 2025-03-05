package com.example.weatherapp.ui.weather_forecast

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.weatherapp.databinding.FragmentWeatherForecastBinding

class WeatherForecastFragment : Fragment() {
    private var _binding: FragmentWeatherForecastBinding? = null
    private val binding get() = _binding!!
    private val adapter = ForecastAdapter()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val viewModel = ViewModelProvider(this)[WeatherForecastViewModel::class.java]

        _binding = FragmentWeatherForecastBinding.inflate(inflater, container, false)
        binding.recyclerView.adapter = adapter

        viewModel.forecast.observe(viewLifecycleOwner) { forecast ->
            adapter.submitList(forecast)
        }

        viewModel.weatherIcon.observe(viewLifecycleOwner) { (iconCode, iconBytes) ->
            adapter.submitIcon(iconCode, iconBytes)
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        viewModel.error.observe(viewLifecycleOwner) { error ->
            error?.let {
                Toast.makeText(context, it, Toast.LENGTH_LONG).show()
            }
        }

        viewModel.fetchForecast()

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}