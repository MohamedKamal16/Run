package com.example.fitness.ui.fragment

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.example.fitness.R
import com.example.fitness.databinding.FragmentStatisticsBinding
import com.example.fitness.ui.viewModel.StatisticViewModel
import com.example.fitness.util.CustomMarkerView
import com.example.fitness.util.TrackingUtility
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import dagger.hilt.android.AndroidEntryPoint
import kotlin.math.round

@AndroidEntryPoint
class StatisticsFragment : Fragment() {

    private lateinit var binding: FragmentStatisticsBinding
    private val viewModel: StatisticViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentStatisticsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        subscribeToObserve()
        setUpBarChart()
    }

    private fun subscribeToObserve() {
        with(binding) {
            viewModel.totalTimeRun.observe(viewLifecycleOwner) {
                it?.let {
                    val totalTimeRun = TrackingUtility.getFormattedStopWatchTime(it)
                    tvTotalTime.text = totalTimeRun
                }
            }
            viewModel.totalDistance.observe(viewLifecycleOwner) {
                it?.let {
                    val km = it / 1000f
                    //Rounds the given value x towards the closest integer with ties rounded towards even integer
                    val totalDistance = round(km * 10f) / 10f
                    //Todo string
                    val totalDistanceString = totalDistance.toString() + getString(R.string.km)
                    tvTotalDistance.text = totalDistanceString
                }
            }
            viewModel.totalAvgSpeed.observe(viewLifecycleOwner) {
                it?.let {
                    //Rounds the given value x towards the closest integer with ties rounded towards even integer
                    val avgSpeed = round(it * 10f) / 10f
                    //Todo string
                    val avgSpeedString = avgSpeed.toString() + getString(R.string.kmh)
                    tvAverageSpeed.text = avgSpeedString
                }
            }
            viewModel.totalCaloriesBurned.observe(viewLifecycleOwner) {
                it?.let {
                    val totalCalories = it.toString() + getString(R.string.Kcal)
                    tvTotalCalories.text = totalCalories
                }
            }
            viewModel.runSortedByDate.observe(viewLifecycleOwner) {
                it?.let {
                    //indices:Returns an IntRange of the valid indices for this collection
                    val allAvgSpeed = it.indices.map { i -> BarEntry(i.toFloat(), it[i].avgSpeed) }
                    val barDataSet =
                        BarDataSet(allAvgSpeed, getString(R.string.AVG_SPEED_OVER_TIME)).apply {
                            valueTextColor = Color.WHITE
                            color = ContextCompat.getColor(requireContext(), R.color.colorAccent)
                        }
                    barChart.data = BarData(barDataSet)

                    barChart.marker=CustomMarkerView(it.reversed(),requireContext(),R.layout.marker_view)
                    barChart.invalidate()
                }
            }
        }
    }

    private fun setUpBarChart() {
        with(binding) {
            barChart.xAxis.apply {
                position = XAxis.XAxisPosition.BOTTOM
                setDrawLabels(false)
                axisLineColor = Color.WHITE
                textColor = Color.WHITE
                setDrawGridLines(false)
            }
            barChart.axisLeft.apply {
                axisLineColor = Color.WHITE
                textColor = Color.WHITE
                setDrawGridLines(false)
            }
            barChart.axisLeft.apply {
                axisLineColor = Color.WHITE
                textColor = Color.WHITE
                setDrawGridLines(false)
            }
            barChart.apply {
                description.text = getString(R.string.AVG_SPEED_OVER_TIME)
                legend.isEnabled = false
            }
        }
    }
}