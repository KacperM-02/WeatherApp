package com.example.weatherapp.ui.settings

import android.content.Context
import android.os.Bundle
import android.view.MenuItem
import android.widget.SearchView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherapp.R
import com.example.weatherapp.databinding.ActivitySettingsBinding
import com.example.weatherapp.data.model.CityData
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class SettingsActivity : AppCompatActivity() {
    private lateinit var searchV : SearchView
    private lateinit var searchRV : RecyclerView

    private lateinit var citiesList : ArrayList<CityData>

    private lateinit var binding: ActivitySettingsBinding
    private lateinit var viewModel: SettingsViewModel

    private lateinit var favoriteCitiesAdapter: FavoriteCitiesAdapter
    private lateinit var citiesAdapter: CitiesAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = getString(R.string.title_settings)

        setupViewModel()
        setupRecyclerView()
        setupRadioGroup()
        observeData()

        searchV = binding.searchView
        searchRV = binding.searchRecyclerView

        searchRV.setHasFixedSize(true)
        searchRV.layoutManager = LinearLayoutManager(this)
        citiesList = loadCityList(this)
        citiesAdapter = CitiesAdapter(citiesList)
        searchRV.adapter = citiesAdapter

        searchV.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                filterList(newText)
                return true
            }
        })
    }

    private fun loadCityList(context: Context): ArrayList<CityData> {
        return try {
            val jsonStr = context.assets.open("city_list.json").bufferedReader().use { it.readText() }
            val listType = object : TypeToken<ArrayList<CityData>>() {}.type
            Gson().fromJson(jsonStr, listType) ?: ArrayList()
        } catch (e: Exception) {
            Toast.makeText(
                context,
                "Błąd wczytywania listy miast: ${e.localizedMessage}",
                Toast.LENGTH_LONG
            ).show()
            ArrayList()
        }
    }

    private fun filterList(query: String?) {
        if (query != null) {
            val filteredList = ArrayList<CityData>()
            for (i in citiesList) {
                if (i.name.lowercase().contains(query)) {
                    filteredList.add(i)
                }
            }

            if (filteredList.isEmpty()) {
                Toast.makeText(this, "No Data found", Toast.LENGTH_SHORT).show()
            } else {
                citiesAdapter.setFilteredList(filteredList)
            }
        }
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