package com.basalbody.app.ui.profile.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding
import com.basalbody.app.base.BaseAdapterWithViewBinding
import com.basalbody.app.databinding.EachRowConnectedBluetoothDevicesBinding
import com.basalbody.app.databinding.EachRowFaqBinding
import com.basalbody.app.extensions.onSafeClick

class FaqListAdapter(
    private var list: ArrayList<String>,
    private var onItemClick: ((String) -> Unit)? = null
) : BaseAdapterWithViewBinding(list) {

    // Keep track of the currently expanded item
    private var expandedPosition: Int = -1

    override fun getViewBinding(viewType: Int, parent: ViewGroup): ViewBinding {
        return EachRowFaqBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val binding = holder.binding as EachRowFaqBinding
        val item = list[position]

        // Show/Hide grpFaq depending on expanded position
        val isExpanded = position == expandedPosition
        binding.grpFaq.visibility = if (isExpanded) View.VISIBLE else View.GONE

        // Toggle on click
        holder.itemView onSafeClick {
            onItemClick?.invoke(item)

            expandedPosition = if (isExpanded) -1 else position
            notifyDataSetChanged() // refresh all items
        }
    }
}