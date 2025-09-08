package com.basalbody.app.ui.profile.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding
import com.basalbody.app.base.BaseAdapterWithViewBinding
import com.basalbody.app.databinding.EachRowConnectedBluetoothDevicesBinding
import com.basalbody.app.extensions.onSafeClick

class ConnectedBluetoothDevicesListAdapter(
    private var list: ArrayList<String>,
    private var onItemClick: ((String) -> Unit)? = null
) : BaseAdapterWithViewBinding(list) {
    override fun getViewBinding(viewType: Int, parent: ViewGroup): ViewBinding {
        return EachRowConnectedBluetoothDevicesBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val binding = holder.binding as EachRowConnectedBluetoothDevicesBinding
        val item = list[position]
        holder.itemView onSafeClick {
            onItemClick?.invoke(item)
        }
    }
}