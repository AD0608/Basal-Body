package com.basalbody.app.ui.home.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.basalbody.app.R
import com.basalbody.app.base.BaseAdapterWithViewBinding
import com.basalbody.app.databinding.EachRowAvailableBluetoothDevicesBinding
import com.basalbody.app.extensions.changeText
import com.basalbody.app.extensions.onSafeClick
import com.basalbody.app.service.BluetoothService

class BluetoothDevicesListAdapter(
    private var onItemClick: (BluetoothService.BluetoothDeviceData) -> Unit,
) : ListAdapter<BluetoothService.BluetoothDeviceData, BluetoothDevicesListAdapter.DeviceViewHolder>(
    DeviceDiffCallback()
) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DeviceViewHolder {
        return DeviceViewHolder(
            EachRowAvailableBluetoothDevicesBinding.inflate(
                LayoutInflater.from(
                    parent.context
                ), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: DeviceViewHolder, position: Int) {
        holder.bind(getItem(position), onItemClick)
    }

    class DeviceViewHolder(var binding: EachRowAvailableBluetoothDevicesBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(
            device: BluetoothService.BluetoothDeviceData,
            onConnectClick: (BluetoothService.BluetoothDeviceData) -> Unit
        ) {
            with(binding) {
                tvBluetoothDeviceName.changeText(device.name ?: "Unknown Device")
                tvBluetoothDeviceType.changeText(device.address)
                btnConnectDevice.setOnClickListener {
                    onConnectClick(device)
                }
            }
        }
    }
}

class DeviceDiffCallback : DiffUtil.ItemCallback<BluetoothService.BluetoothDeviceData>() {
    override fun areItemsTheSame(
        oldItem: BluetoothService.BluetoothDeviceData,
        newItem: BluetoothService.BluetoothDeviceData
    ): Boolean {
        return oldItem.address == newItem.address
    }

    override fun areContentsTheSame(
        oldItem: BluetoothService.BluetoothDeviceData,
        newItem: BluetoothService.BluetoothDeviceData
    ): Boolean {
        // Simple comparison of data class contents
        return oldItem == newItem
    }
}