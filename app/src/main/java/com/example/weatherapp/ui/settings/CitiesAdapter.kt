package com.example.weatherapp.ui.settings

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherapp.R
import com.example.weatherapp.data.model.CityData

class CitiesAdapter(private var citiesList: List<CityData>) : RecyclerView.Adapter<CitiesAdapter.CityViewHolder>()
{
    inner class CityViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
    {
        val cityNameTV: TextView = itemView.findViewById(R.id.cityName)
    }

    fun setFilteredList(citiesList: List<CityData>)
    {
        this.citiesList = citiesList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CityViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_favorite_city, parent, false)
        return CityViewHolder(view)
    }

    override fun getItemCount(): Int {
        return citiesList.size
    }

    override fun onBindViewHolder(holder: CityViewHolder, position: Int) {
        holder.cityNameTV.text = citiesList[position].name
    }
}