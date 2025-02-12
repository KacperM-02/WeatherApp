package com.example.weatherapp.ui.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.weatherapp.databinding.FragmentSettingsBinding

class SettingsFragment : Fragment() {
    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: SettingsViewModel
    private lateinit var favoriteCitiesAdapter: FavoriteCitiesAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this)[SettingsViewModel::class.java]
        setupRecyclerView()
        setupUnitsRadioGroup()
        return binding.root
    }

    private fun setupRecyclerView() {
        favoriteCitiesAdapter = FavoriteCitiesAdapter()
        binding.favoriteCitiesList.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = favoriteCitiesAdapter
        }

        viewModel.favoriteCities.observe(viewLifecycleOwner) { cities ->
            favoriteCitiesAdapter.updateCities(cities)
        }
    }

    private fun setupUnitsRadioGroup() {
        when (viewModel.getUnits()) {
            "standard" -> binding.standardUnits.isChecked = true
            "metric" -> binding.metricUnits.isChecked = true
            "imperial" -> binding.imperialUnits.isChecked = true
        }

        binding.unitsRadioGroup.setOnCheckedChangeListener { _, checkedId ->
            val units = when (checkedId) {
                binding.standardUnits.id -> "standard"
                binding.metricUnits.id -> "metric"
                binding.imperialUnits.id -> "imperial"
                else -> "metric"
            }
            viewModel.saveUnits(units)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
} 