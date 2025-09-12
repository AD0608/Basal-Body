package com.basalbody.app.ui.home.fragment

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import com.basalbody.app.R
import com.basalbody.app.base.BaseFragment
import com.basalbody.app.databinding.FragmentInsightsBinding
import com.basalbody.app.extensions.changeText
import com.basalbody.app.extensions.gone
import com.basalbody.app.ui.home.adapter.CycleRegularitiesInsightListAdapter
import com.basalbody.app.ui.home.viewmodel.HomeViewModel
import com.basalbody.app.utils.CommonUtils.dpToPx
import com.basalbody.app.utils.GridSpacingItemDecoration
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter

class InsightsFragment : BaseFragment<HomeViewModel, FragmentInsightsBinding>(
    FragmentInsightsBinding::inflate) {
    val TAG = "InsightsFragment"

    override fun getViewBinding(): FragmentInsightsBinding = FragmentInsightsBinding.inflate(layoutInflater)

    private var cycleInsightsList = arrayListOf<CycleInsight>().apply {
        add(CycleInsight("Jan", "28"))
        add(CycleInsight("Feb", "28"))
        add(CycleInsight("Mar", "26"))
        add(CycleInsight("Apr", "28"))

        add(CycleInsight("May", "28"))
        add(CycleInsight("Jun", "26"))
        add(CycleInsight("Jul", "28"))
        add(CycleInsight("Aug", "26"))
        add(CycleInsight("Sep", "28"))
        add(CycleInsight("Oct", "28"))
        add(CycleInsight("Nov", "26"))
        add(CycleInsight("Dec", "28"))
    }

    private val cycleRegularitiesInsightListAdapter by lazy {
        CycleRegularitiesInsightListAdapter(cycleInsightsList) {
            Log.e(TAG, "cycleRegularitiesInsightListAdapter: ${it.month}")
        }
    }

    override fun initSetup() {
        setupUI()
    }

    private fun setupUI() {
        Log.e(TAG, "setupUI()")
        binding.apply {
            toolBar.tvTitle.changeText(R.string.item_insights)
            toolBar.ivBack.gone()
            tvDate.changeText("Jul 22-17")

            val spanCount = 4 // number of columns
            val spacing = dpToPx(requireContext(), 10)
            val includeEdge = false


            rvCycleRegularityInsights.layoutManager = GridLayoutManager(requireContext(), spanCount)
            rvCycleRegularityInsights.adapter = cycleRegularitiesInsightListAdapter
            rvCycleRegularityInsights.isNestedScrollingEnabled = false
            rvCycleRegularityInsights.addItemDecoration(
                GridSpacingItemDecoration(
                    spanCount,
                    spacing,
                    includeEdge
                )
            )
        }

        setChartData()
    }

    private fun setChartData() {
        Log.e(TAG, "setChartData()")

        binding.apply {
            // Sample data (Temps for Jan–Jul)
            val entries = listOf(
                BarEntry(0f, 28f),
                BarEntry(1f, 32f),
                BarEntry(2f, 25f),
                BarEntry(3f, 36f),
                BarEntry(4f, 32f),
                BarEntry(5f, 31f),
                BarEntry(6f, 25f)
            )

            val dataSet = BarDataSet(entries, "Temperature (°C)").apply {
                color = Color.parseColor("#17955D") // green
                valueTextColor = Color.BLACK
                valueTextSize = 12f
                setDrawValues(true)
            }

            val barData = BarData(dataSet).apply {
                barWidth = 0.5f
            }

            barChart.data = barData

            // X-Axis labels
            val months = listOf("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul")
            barChart.xAxis.apply {
                valueFormatter = IndexAxisValueFormatter(months)
                position = XAxis.XAxisPosition.BOTTOM
                granularity = 1f
                setDrawGridLines(false)
                textSize = 12f
            }

            // Y-Axis
            barChart.axisLeft.apply {
                axisMinimum = 0f
                axisMaximum = 40f
                granularity = 5f
            }
            barChart.axisRight.isEnabled = false

            // Chart styling
            barChart.description.isEnabled = false
            barChart.legend.isEnabled = false
            barChart.animateY(1000)
        }
    }

    override fun listeners() {
        binding.apply {

        }
    }

    data class CycleInsight(var month: String, var days: String)

}