package com.basalbody.app.ui.home.fragment

import android.graphics.Color
import android.util.Log
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.basalbody.app.R
import com.basalbody.app.base.BaseFragment
import com.basalbody.app.base.FlowInFragment
import com.basalbody.app.databinding.FragmentInsightsBinding
import com.basalbody.app.extensions.changeText
import com.basalbody.app.extensions.formatFertileWindow
import com.basalbody.app.extensions.getSortMonth
import com.basalbody.app.extensions.gone
import com.basalbody.app.extensions.notNull
import com.basalbody.app.model.BaseResponse
import com.basalbody.app.model.response.FaqResponse
import com.basalbody.app.model.response.GetInsightsResponse
import com.basalbody.app.model.response.LogoutResponse
import com.basalbody.app.model.response.MonthlyInsight
import com.basalbody.app.ui.home.adapter.CycleRegularitiesInsightListAdapter
import com.basalbody.app.ui.home.viewmodel.HomeViewModel
import com.basalbody.app.utils.CommonUtils.dpToPx
import com.basalbody.app.utils.GridSpacingItemDecoration
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class InsightsFragment : BaseFragment<HomeViewModel, FragmentInsightsBinding>(
    FragmentInsightsBinding::inflate
) {
    val TAG = "InsightsFragment"

    override fun getViewBinding(): FragmentInsightsBinding =
        FragmentInsightsBinding.inflate(layoutInflater)

    private var cycleInsightsList = ArrayList<MonthlyInsight>()

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
        viewModel.callGetLogsInsightsApi()
        binding.apply {
            toolBar.tvTitle.changeText(R.string.item_insights)
            toolBar.ivBack.gone()

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
    }

    override fun listeners() {
        binding.apply {

        }
    }

    override fun addObserver() {
        Log.e(TAG, "addObserver()")

        lifecycleScope.launch {
            viewModel.callGetLogsInsightsApiStateFlow.collect {
                FlowInFragment<BaseResponse<GetInsightsResponse>>(
                    data = it,
                    fragment = this@InsightsFragment,
                    shouldShowErrorMessage = true,
                    shouldShowLoader = true,
                    onSuccess = ::handleGetLogsInsightsResponse,
                )
            }
        }
    }

    private fun handleGetLogsInsightsResponse(response: BaseResponse<GetInsightsResponse>?) {
        if (response.notNull() && response?.status == true) {
            Log.e(TAG, "handleGetLogsInsightsResponse()")

            response?.data?.fertileWindow?.let {

                val date = formatFertileWindow(it.start ?: "0", it.end ?: "0")
                binding.tvDate.changeText(date)
            }

            response?.data?.monthlyInsights?.let {
                cycleInsightsList.addAll(it)
            }
            cycleRegularitiesInsightListAdapter.notifyDataSetChanged()

            setChartData()
        }
    }

    private fun setChartData() {
        Log.e(TAG, "setChartData()")

        val entries = mutableListOf<BarEntry>()
        val monthLabels = mutableListOf<String>()  // "Dec", "Jan", "Feb", ...

        cycleInsightsList.forEachIndexed { index, item ->
            // Example: "December-2024" → "Dec"
            val monthShort = getSortMonth(item.month ?: "")
            monthLabels.add(monthShort)

            // Add Bar Entry (X=index, Y=avg temp)
            entries.add(BarEntry(index.toFloat(), item.averageTemperature?.toFloat() ?: 0f))
        }

        binding.apply {

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

            barChart.xAxis.apply {
                valueFormatter = IndexAxisValueFormatter(monthLabels)
                position = XAxis.XAxisPosition.BOTTOM
                granularity = 1f
                setDrawGridLines(false)
                textSize = 12f
            }

            val maxTemp = cycleInsightsList.maxOfOrNull { it.averageTemperature ?: 0.0 } ?: 0.0
            val yMax = (maxTemp + 5).toFloat()   // add 5 units for top spacing

            // Y-Axis
            barChart.axisLeft.apply {
                axisMinimum = 0f
                axisMaximum = yMax
                granularity = 5f
            }
            barChart.axisRight.isEnabled = false

            // Chart styling
            barChart.description.isEnabled = false
            barChart.legend.isEnabled = false
            barChart.animateY(1000)
            barChart.invalidate()
        }
    }
}