package com.example.weatherapp.ui.settings

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherapp.R

class FavoriteCitiesAdapter(
    private var cities: Map<String, Int>,
    private val onFavoriteCityClickListener: OnFavoriteCityClickListener
    ) : RecyclerView.Adapter<FavoriteCitiesAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_favorite_city, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val citiesList = cities.keys.toList()
        holder.cityName.text = citiesList[position]
    }

    override fun getItemCount() = cities.size


    interface OnFavoriteCityClickListener {
        fun onFavoriteCityClick(cityId: Int)
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val cityName: TextView = view.findViewById(R.id.cityName)

        init {
            view.setOnClickListener {
                val position: Int = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onFavoriteCityClickListener.onFavoriteCityClick(cities.values.toList()[position])
                }
            }
        }
    }


} 