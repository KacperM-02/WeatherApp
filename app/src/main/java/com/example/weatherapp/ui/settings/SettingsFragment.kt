package com.example.weatherapp.ui.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.weatherapp.databinding.FragmentSettingsBinding

class SettingsFragment : Fragment() {
    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: SettingsViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this)[SettingsViewModel::class.java]

        binding.cityInput.setText(viewModel.getCity())
        when (viewModel.getUnits()) {
            "standard" -> binding.standardUnits.isChecked = true
            "metric" -> binding.metricUnits.isChecked = true
            "imperial" -> binding.imperialUnits.isChecked = true
        }

        binding.saveButton.setOnClickListener {
            val city = binding.cityInput.text.toString()
            val units = when {
                binding.standardUnits.isChecked -> "standard"
                binding.metricUnits.isChecked -> "metric"
                binding.imperialUnits.isChecked -> "imperial"
                else -> "metric"
            }

            if (city.isNotBlank()) {
                viewModel.saveSettings(city, units)
                Toast.makeText(context, "Ustawienia zapisane", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "Wprowadź nazwę miasta", Toast.LENGTH_SHORT).show()
            }
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
} 