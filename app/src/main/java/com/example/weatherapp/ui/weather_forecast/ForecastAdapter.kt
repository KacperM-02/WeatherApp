package com.example.weatherapp.ui.weather_forecast

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherapp.R
import com.example.weatherapp.data.model.ForecastItem
import com.example.weatherapp.databinding.ItemForecastBinding

class ForecastAdapter : RecyclerView.Adapter<ForecastAdapter.ForecastViewHolder>() {
    private var items = listOf<ForecastItem>()
    private var icons = listOf<ByteArray>()

    @SuppressLint("NotifyDataSetChanged")
    fun submitList(newItems: List<ForecastItem>) {
        items = newItems
        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun submitIcon(iconBytesList: List<ByteArray>) {
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
        holder.bind(items[position])
    }

    override fun getItemCount() = items.size

    inner class ForecastViewHolder(
        private val binding: ItemForecastBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: ForecastItem) {
            binding.description.text = item.weather.firstOrNull()?.description ?: ""
            binding.temp.text = binding.root.context.getString(R.string.temperature_format, item.main.temp)
            binding.feelsLike.text = binding.root.context.getString(R.string.temperature_format, item.main.feelsLike)

//            item.weather.firstOrNull()?.icon?.let { iconCode ->
//                icons.let { iconBytes ->
//                    val bitmap = BitmapFactory.decodeByteArray(iconBytes, 0, iconBytes.size)
//                    binding.weatherIcon.setImageBitmap(bitmap)
//                }
//            }
        }
    }
}