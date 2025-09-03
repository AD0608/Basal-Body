package com.basalbody.app.ui.home.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding
import com.basalbody.app.base.BaseAdapterWithViewBinding
import com.basalbody.app.databinding.EachRowAvailableBluetoothDevicesBinding

class BluetoothDevicesListAdapter (private var list : ArrayList<String>) : BaseAdapterWithViewBinding(list) {
    override fun getViewBinding(viewType: Int, parent: ViewGroup): ViewBinding {
        return EachRowAvailableBluetoothDevicesBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val binding = holder.binding as EachRowAvailableBluetoothDevicesBinding
        val item = list[position]

    }
}