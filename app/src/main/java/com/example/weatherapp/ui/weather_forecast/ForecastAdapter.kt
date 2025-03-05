package com.example.weatherapp.ui.weather_forecast

import android.annotation.SuppressLint
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherapp.R
import com.example.weatherapp.data.model.ForecastItem
import com.example.weatherapp.databinding.ItemForecastBinding
import java.text.SimpleDateFormat
import java.util.*

class ForecastAdapter : RecyclerView.Adapter<ForecastAdapter.ForecastViewHolder>() {
    private var items = listOf<ForecastItem>()
    private var icons = mutableMapOf<String, ByteArray>()

    @SuppressLint("NotifyDataSetChanged")
    fun submitList(newItems: List<ForecastItem>) {
        items = newItems
        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun submitIcon(iconCode: String, iconBytes: ByteArray) {
        icons[iconCode] = iconBytes
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
        holder.bind(items[position])
    }

    override fun getItemCount() = items.size

    inner class ForecastViewHolder(
        private val binding: ItemForecastBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: ForecastItem) {
            val dateFormat = SimpleDateFormat("dd.MM HH:mm", Locale.getDefault())
            binding.dateTime.text = dateFormat.format(Date(item.dt * 1000))
            binding.temperature.text = binding.root.context.getString(R.string.temperature_format, item.main.temp)
            binding.description.text = item.weather.firstOrNull()?.description ?: ""

            item.weather.firstOrNull()?.icon?.let { iconCode ->
                icons[iconCode]?.let { iconBytes ->
                    val bitmap = BitmapFactory.decodeByteArray(iconBytes, 0, iconBytes.size)
                    binding.weatherIcon.setImageBitmap(bitmap)
                }
            }
        }
    }
}