package com.example.fitness.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.fitness.databinding.ItemRunBinding
import com.example.fitness.model.db.Run
import com.example.fitness.util.TrackingUtility
import java.text.SimpleDateFormat
import java.util.*

class RunAdapter : RecyclerView.Adapter<RunAdapter.RunViewHolder>() {

    inner class RunViewHolder(val binding: ItemRunBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RunViewHolder {
        return RunViewHolder(ItemRunBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: RunViewHolder, position: Int) {
       val run =differ.currentList[position]
        with(holder.binding){
            //hold all view can remove it and replace this with ivRunImage.context
            holder.itemView.apply {
                //pic
                Glide.with(this).load(run.img).into(ivRunImage)
                //data
                val calender=Calendar.getInstance().apply {
                    timeInMillis=run.timeMillSec
                }
                val dataFormat =SimpleDateFormat("dd.MM.yy",Locale.getDefault())
                tvDate.text=dataFormat.format(calender.time)
                //avgSpeed
                val avgSpeed="${run.avgSpeed}Km/h"
                tvAvgSpeed.text=avgSpeed
                //distance
                val distanceInKm="${run.distanceInMeters/1000f}Km"
                tvDistance.text=distanceInKm
                //time
                tvTime.text =TrackingUtility.getFormattedStopWatchTime(run.timeMillSec)
                //calories
                val calories="${run.caloriesBurnt}Kcal"
                tvCalories.text=calories
            }
        }

    }

    override fun getItemCount(): Int {
        return  differ.currentList.size
    }

    private val differCallBack = object : DiffUtil.ItemCallback<Run>() {
        override fun areItemsTheSame(oldItem: Run, newItem: Run): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Run, newItem: Run): Boolean {
            return oldItem.hashCode() == newItem.hashCode()
        }
    }

    //Async list differ take two list and compare them to change the difference only it run on background
    val differ = AsyncListDiffer(this, differCallBack)

    fun submitList(list: List<Run>) = differ.submitList(list)
}