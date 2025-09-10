package com.basalbody.app.ui.home.adapter

import android.content.Context
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding
import com.basalbody.app.base.BaseAdapterWithViewBinding
import com.basalbody.app.databinding.EachRowNotificationBinding

class NotificationListAdapter(
    private val context: Context,
    private var list: ArrayList<String>,
    private var onItemClick: ((String) -> Unit)? = null
) : BaseAdapterWithViewBinding(list) {
    override fun getViewBinding(
        viewType: Int,
        parent: ViewGroup
    ): ViewBinding {
        return EachRowNotificationBinding.inflate(
            android.view.LayoutInflater.from(parent.context),
            parent,
            false
        )
    }
    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val binding = holder.binding as EachRowNotificationBinding
        val item = list[position]
        binding.apply {
            // You can bind your data to the views here
            // For example:
            // textView.text = item
            root.setOnClickListener {
                onItemClick?.invoke(item)
            }
        }
    }

    fun removeAt(position: Int) {
        if (position < 0 || position >= list.size) return
        list.removeAt(position)
        notifyItemRemoved(position)
    }
}