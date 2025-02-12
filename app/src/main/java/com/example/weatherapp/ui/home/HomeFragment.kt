package com.example.weatherapp.ui.home

import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.weatherapp.R
import com.example.weatherapp.databinding.FragmentHomeBinding
import com.example.weatherapp.ui.settings.SettingsViewModel

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var homeViewModel: HomeViewModel
    private lateinit var settingsViewModel: SettingsViewModel
    private var currentCity: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        homeViewModel = ViewModelProvider(this)[HomeViewModel::class.java]
        settingsViewModel = ViewModelProvider(this)[SettingsViewModel::class.java]
        homeViewModel.fetchWeatherData("Warsaw")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        setupObservers()
        setupFavoriteButton()

        return root
    }

    private fun setupObservers() {
        homeViewModel.weatherData.observe(viewLifecycleOwner) { weatherData ->
            binding.textHome.text = weatherData
            currentCity = weatherData.split("\n").firstOrNull()?.removePrefix("Miasto: ")?.split(",")?.firstOrNull()?.trim() ?: ""
            updateFavoriteIcon()
        }

        homeViewModel.weatherIcon.observe(viewLifecycleOwner) { iconBytes ->
            val bitmap = BitmapFactory.decodeByteArray(iconBytes, 0, iconBytes.size)
            binding.weatherIcon.setImageBitmap(bitmap)
        }

        homeViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        homeViewModel.error.observe(viewLifecycleOwner) { error ->
            error?.let {
                Toast.makeText(context, it, Toast.LENGTH_LONG).show()
            }
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