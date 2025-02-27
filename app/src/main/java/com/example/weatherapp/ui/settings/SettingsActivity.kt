package com.example.weatherapp.ui.settings

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.weatherapp.databinding.ActivitySettingsBinding

class SettingsActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySettingsBinding
    private lateinit var viewModel: SettingsViewModel
    private lateinit var favoriteCitiesAdapter: FavoriteCitiesAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Konfiguracja ActionBar
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = getString(com.example.weatherapp.R.string.title_settings)

        setupViewModel()
        setupRecyclerView()
        setupRadioGroup()
        observeData()
    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(this)[SettingsViewModel::class.java]
    }

    private fun setupRecyclerView() {
        favoriteCitiesAdapter = FavoriteCitiesAdapter()
        binding.favoriteCitiesList.apply {
            adapter = favoriteCitiesAdapter
            layoutManager = LinearLayoutManager(this@SettingsActivity)
        }
    }

    private fun setupRadioGroup() {
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

    private fun observeData() {
        viewModel.favoriteCities.observe(this) { cities ->
            favoriteCitiesAdapter.updateCities(cities)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
} 