package com.example.weatherapp.ui.settings

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherapp.R

class FavoriteCitiesAdapter : RecyclerView.Adapter<FavoriteCitiesAdapter.ViewHolder>() {
    private var cities: List<String> = emptyList()

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val cityName: TextView = view.findViewById(R.id.cityName)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_favorite_city, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.cityName.text = cities[position]
    }

    override fun getItemCount() = cities.size

    fun updateCities(newCities: List<String>) {
        cities = newCities
        notifyDataSetChanged()
    }
} 