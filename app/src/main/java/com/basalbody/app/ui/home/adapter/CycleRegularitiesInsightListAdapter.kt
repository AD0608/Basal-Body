package com.basalbody.app.ui.home.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding
import com.basalbody.app.base.BaseAdapterWithViewBinding
import com.basalbody.app.databinding.EachRowCycleRegularityInsightsBinding
import com.basalbody.app.extensions.getSortMonth
import com.basalbody.app.extensions.onSafeClick
import com.basalbody.app.model.response.MonthlyInsight
import com.basalbody.app.ui.home.fragment.InsightsFragment

class CycleRegularitiesInsightListAdapter(
    private var list: ArrayList<MonthlyInsight>,
    private var onItemClick: ((MonthlyInsight) -> Unit)? = null
) : BaseAdapterWithViewBinding(list) {
    override fun getViewBinding(viewType: Int, parent: ViewGroup): ViewBinding {
        return EachRowCycleRegularityInsightsBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val binding = holder.binding as EachRowCycleRegularityInsightsBinding
        val item = list[position]
        binding.apply {
            tvMonth.text = getSortMonth(item.month ?: "")
            tvDays.text = item.days.toString().plus("\nDays")
        }
        binding.root onSafeClick {
            onItemClick?.invoke(item)
        }

    }
}