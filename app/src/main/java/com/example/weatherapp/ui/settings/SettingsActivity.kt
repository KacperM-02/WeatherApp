package com.example.weatherapp.ui.settings

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.JsonReader
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.SearchView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherapp.R
import com.example.weatherapp.databinding.ActivitySettingsBinding
import com.example.weatherapp.data.model.CityData
import com.example.weatherapp.data.preferences.WeatherSettingsPreferences
import java.io.InputStreamReader

class SettingsActivity : AppCompatActivity(), CitySearchAdapter.OnCityClickListener, FavoriteCitiesAdapter.OnFavoriteCityClickListener {
    private lateinit var searchV : SearchView
    private lateinit var searchRV : RecyclerView

    private lateinit var binding: ActivitySettingsBinding

    private lateinit var favoriteCitiesAdapter: FavoriteCitiesAdapter
    private lateinit var citySearchAdapter: CitySearchAdapter

    private lateinit var weatherSettingsPreferences: WeatherSettingsPreferences

    private val intent = Intent()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = getString(R.string.title_settings)

        weatherSettingsPreferences = WeatherSettingsPreferences(this)

        setupBindings()
        setupRecyclerView()
        setupRadioGroup()
        setupSearchView()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                setResult(Activity.RESULT_OK, intent)
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onCityClick(cityId: Int) {
        Log.d("SettingsActivity", "onCityClick: cityId: $cityId")
        intent.putExtra("chosenCityId", cityId)
    }

    override fun onFavoriteCityClick(cityId: Int) {
        Log.d("SettingsActivity", "onFavoriteCityClick: cityId: $cityId")
        intent.putExtra("chosenCityId", cityId)
    }


    private fun setupBindings() {
        searchV = binding.searchView
        searchRV = binding.searchRecyclerView
    }

    private fun setupRecyclerView() {
        val favoriteCities = weatherSettingsPreferences.loadFavoriteCities()
        favoriteCitiesAdapter = FavoriteCitiesAdapter(favoriteCities, this)
        binding.favoriteCitiesList.apply {
            adapter = favoriteCitiesAdapter
            layoutManager = LinearLayoutManager(this@SettingsActivity)
        }

        searchRV.visibility = View.GONE
        searchRV.setHasFixedSize(true)
        citySearchAdapter = CitySearchAdapter(ArrayList(), this)
        binding.searchRecyclerView.apply {
            adapter = citySearchAdapter
            layoutManager = LinearLayoutManager(this@SettingsActivity)
        }
    }

    private fun setupRadioGroup() {
        when (weatherSettingsPreferences.getUnits()) {
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
            weatherSettingsPreferences.saveUnits(units)
            intent.putExtra("units", units)
        }
    }

    private fun setupSearchView() {
        searchV.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (!query.isNullOrEmpty()) {
                    val filteredCities = loadFilteredUniqueCities(this@SettingsActivity, query)
                    if (filteredCities.isEmpty()) {
                        searchRV.visibility = View.GONE
                        Toast.makeText(this@SettingsActivity, "No Data found", Toast.LENGTH_SHORT).show()
                    } else {
                        citySearchAdapter.updateCitiesList(filteredCities)
                        searchRV.visibility = View.VISIBLE
                    }
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if(newText.isNullOrEmpty()) searchRV.visibility = View.GONE
                return false
            }
        })
    }


    fun loadFilteredUniqueCities(context: Context, filter: String): ArrayList<CityData> {
        val filteredCities = ArrayList<CityData>()
        val seenNames = mutableSetOf<String>()

        context.assets.open("city_list.json").use { inputStream ->
            InputStreamReader(inputStream, "UTF-8").use { isr ->
                JsonReader(isr).use { reader ->
                    reader.beginArray()  // Rozpoczęcie czytania tablicy JSON
                    while (reader.hasNext()) {
                        var id = 0
                        var name = ""
                        var country = ""

                        reader.beginObject()  // Rozpoczęcie czytania obiektu
                        while (reader.hasNext()) {
                            when (reader.nextName()) {
                                "id" -> id = reader.nextInt()
                                "name" -> name = reader.nextString()
                                "country" -> country = reader.nextString()
                                else -> reader.skipValue()  // Pomijamy inne pola
                            }
                        }
                        reader.endObject()

                        // Dodajemy obiekt tylko jeśli nazwa zawiera filtr i nie była jeszcze dodana
                        if (name.contains(filter, ignoreCase = true) && seenNames.add(name)) {
                            filteredCities.add(CityData(id, name, country))
                        }
                    }
                    reader.endArray()  // Zakończenie czytania tablicy JSON
                }
            }
        }
        return filteredCities
    }
} 