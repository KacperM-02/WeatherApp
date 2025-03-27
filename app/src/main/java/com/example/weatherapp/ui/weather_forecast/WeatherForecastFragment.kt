package com.example.weatherapp.ui.weather_forecast

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.example.weatherapp.data.model.ForecastResponse
import com.example.weatherapp.databinding.FragmentWeatherForecastBinding

class WeatherForecastFragment : Fragment() {
    private var _binding: FragmentWeatherForecastBinding? = null
    private val binding get() = _binding!!
    private val adapter = ForecastAdapter()
    private val weatherForecastViewModel: WeatherForecastViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWeatherForecastBinding.inflate(inflater, container, false)
        binding.recyclerView.adapter = adapter
        setupObservers()
        return binding.root
    }

    private fun setupObservers() {
        val forecastObserver = Observer<ForecastResponse> { forecast ->
            adapter.updateItems(forecast)
        }

        val weatherIconsListObserver = Observer<List<ByteArray>> { iconBytesList ->
            adapter.updateIcons(iconBytesList)
        }

        val isLoadingObserver = Observer<Boolean> { isLoading ->
            if(isLoading)
            {
                binding.progressBar.visibility = View.VISIBLE
                binding.recyclerView.visibility = View.GONE
            }
            else {
                binding.progressBar.visibility = View.GONE
                binding.recyclerView.visibility = View.VISIBLE
            }
        }

        val errorObserver = Observer<String> { error ->
            error.let {
                Toast.makeText(context, it, Toast.LENGTH_LONG).show()
            }
        }

        val tempUnitsObserver = Observer<String> { tempUnits ->
            adapter.updateTempUnits(tempUnits)
        }

        weatherForecastViewModel.forecast.observe(viewLifecycleOwner, forecastObserver)
        weatherForecastViewModel.forecastIcons.observe(viewLifecycleOwner, weatherIconsListObserver)
        weatherForecastViewModel.isLoading.observe(viewLifecycleOwner, isLoadingObserver)
        weatherForecastViewModel.error.observe(viewLifecycleOwner, errorObserver)
        weatherForecastViewModel.tempUnits.observe(viewLifecycleOwner, tempUnitsObserver)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}