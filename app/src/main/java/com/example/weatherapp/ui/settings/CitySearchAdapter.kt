package com.example.weatherapp.ui.settings

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherapp.R
import com.example.weatherapp.data.model.CityData

class CitySearchAdapter(
    private var citiesList: List<CityData>,
    private val onCityClickListener: OnCityClickListener
    ) : RecyclerView.Adapter<CitySearchAdapter.CityViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CityViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_favorite_city, parent, false)
        return CityViewHolder(view)
    }

    interface OnCityClickListener {
        fun onCityClick(cityId: Int)
    }

    override fun getItemCount(): Int {
        return citiesList.size
    }

    override fun onBindViewHolder(holder: CityViewHolder, position: Int) {
        holder.cityNameTV.text = "%s, %s".format(citiesList[position].name, citiesList[position].country)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateCitiesList(newCitiesList: List<CityData>)
    {
        this.citiesList = newCitiesList
        notifyDataSetChanged()
    }

    inner class CityViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
    {
        val cityNameTV: TextView = itemView.findViewById(R.id.cityName)
        
        init {
            itemView.setOnClickListener {
                val position: Int = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onCityClickListener.onCityClick(citiesList[position].id)
                }
            }
        }

    }
}