package com.basalbody.app.ui.home.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding
import com.basalbody.app.R
import com.basalbody.app.base.BaseAdapterWithViewBinding
import com.basalbody.app.databinding.EachRowTodaysActivityBinding
import com.basalbody.app.extensions.changeBackground
import com.basalbody.app.extensions.changeText
import com.basalbody.app.extensions.onSafeClick

class TodaysActivityListAdapter(
    private val context: Context,
    private var list: ArrayList<String>,
    private var onItemClick: ((String) -> Unit)? = null
) : BaseAdapterWithViewBinding(list) {
    override fun getViewBinding(viewType: Int, parent: ViewGroup): ViewBinding {
        return EachRowTodaysActivityBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val binding = holder.binding as EachRowTodaysActivityBinding
        val item = list[position]
        binding.apply {
            when (item) {
                "menstruation" -> {
                    root.changeBackground(R.drawable.bg_today_menstruation_activity)
                    tvActivityTitle.changeText(context.getString(R.string.label_menstruation))
                    imgActivity.setImageResource(R.drawable.ic_menstruation)
                }

                "intercourse" -> {
                    root.changeBackground(R.drawable.bg_today_intercourse_activity)
                    tvActivityTitle.changeText(context.getString(R.string.label_intercourse))
                    imgActivity.setImageResource(R.drawable.ic_intercourse)
                }

                "temperatureTrend" -> {
                    root.changeBackground(R.drawable.bg_today_temperature_trend_activity)
                    tvActivityTitle.changeText(context.getString(R.string.label_temperature_trend))
                    imgActivity.setImageResource(R.drawable.ic_intercourse)
                }
            }
            root onSafeClick {
                onItemClick?.invoke(item)
            }
        }

    }
}