package com.example.weatherapp.ui.weather_forecast

import android.annotation.SuppressLint
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherapp.R
import com.example.weatherapp.data.model.ForecastItem
import com.example.weatherapp.data.model.ForecastResponse
import com.example.weatherapp.databinding.ItemForecastBinding
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

class ForecastAdapter : RecyclerView.Adapter<ForecastAdapter.ForecastViewHolder>() {
    private var items = listOf<ForecastItem>()
    private var icons = listOf<ByteArray>()
    private var tempUnits = String()

    @SuppressLint("NotifyDataSetChanged")
    fun updateItems(newItems: ForecastResponse) {
        items = newItems.list
        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateIcons(iconBytesList: List<ByteArray>) {
        icons = iconBytesList
        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateTempUnits(units: String) {
        tempUnits = when(units) {
            "standard" -> "K"
            "metric" -> "°C"
            "imperial" -> "°F"
            else -> "°C"
        }
        notifyDataSetChanged()
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ForecastViewHolder {
        return ForecastViewHolder(
            ItemForecastBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ForecastViewHolder, position: Int) {
        holder.bind(items[position], icons[position])
    }

    override fun getItemCount() = items.size


    inner class ForecastViewHolder(
        private val binding: ItemForecastBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: ForecastItem, icon: ByteArray) {
            val dateTime = LocalDateTime.parse(item.dt_txt, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
            val day = dateTime.dayOfWeek.getDisplayName(TextStyle.FULL, Locale("eng"))

            binding.day.text = binding.root.context.getString(R.string.day_format, day)
            binding.description.text = binding.root.context.getString(R.string.description_format, item.weather.firstOrNull()?.description)
            binding.temp.text = binding.root.context.getString(R.string.temperature_format, item.main.temp, tempUnits)
            binding.feelsLike.text = binding.root.context.getString(R.string.feels_like_format, item.main.feels_like, tempUnits)

            val bitmap = BitmapFactory.decodeByteArray(icon, 0, icon.size)
            binding.weatherIcon.setImageBitmap(bitmap)
        }
    }
}