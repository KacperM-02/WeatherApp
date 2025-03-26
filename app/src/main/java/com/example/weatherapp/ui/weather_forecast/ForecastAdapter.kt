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

    @SuppressLint("NotifyDataSetChanged")
    fun submitList(newItems: ForecastResponse) {
        items = newItems.list
        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun submitIcons(iconBytesList: List<ByteArray>) {
        icons = iconBytesList
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

            binding.day.text = day
            binding.description.text = item.weather.firstOrNull()?.description ?: ""
            binding.temp.text = binding.root.context.getString(R.string.temperature_format, item.main.temp)
            binding.feelsLike.text = binding.root.context.getString(R.string.temperature_format, item.main.feelsLike)

            val bitmap = BitmapFactory.decodeByteArray(icon, 0, icon.size)
            binding.weatherIcon.setImageBitmap(bitmap)
        }
    }
}