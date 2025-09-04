package com.basalbody.app.ui.home.fragment

import androidx.core.graphics.toColorInt
import com.basalbody.app.R
import com.basalbody.app.base.BaseFragment
import com.basalbody.app.databinding.FragmentBluetoothBinding
import com.basalbody.app.extensions.addRippleWaves
import com.basalbody.app.extensions.gone
import com.basalbody.app.extensions.onSafeClick
import com.basalbody.app.extensions.removeRippleWaves
import com.basalbody.app.extensions.visible
import com.basalbody.app.ui.home.adapter.BluetoothDevicesListAdapter
import com.basalbody.app.ui.home.viewmodel.HomeViewModel

class BluetoothFragment :
    BaseFragment<HomeViewModel, FragmentBluetoothBinding>(FragmentBluetoothBinding::inflate) {
    override fun getViewBinding(): FragmentBluetoothBinding =
        FragmentBluetoothBinding.inflate(layoutInflater)

    private val availableDevicesAdapter by lazy {
        BluetoothDevicesListAdapter(arrayListOf("", "", "", "", "", ""), ::onConnectDeviceClick)
    }

    override fun initSetup() {
        binding.apply {
            imgBluetooth.setImageResource(R.drawable.ic_bluetooth_blue)
            clConnectedDevice.gone()
            grpAvailableDevices.gone()
            rvAvailableDevices.adapter = availableDevicesAdapter
        }
    }

    override fun listeners() {
        binding.apply {
            btnScanForDevices.setOnClickListener {
                clConnectedDevice.gone()
                grpAvailableDevices.visible()
                imgBluetooth.addRippleWaves(color = "#407FFF".toColorInt())
            }

            btnChangeConnectionStatus onSafeClick {
                imgBluetooth.setImageResource(R.drawable.ic_bluetooth_blue)
                grpAvailableDevices.visible()
                clConnectedDevice.gone()
                btnScanForDevices.visible()
                imgBluetooth.addRippleWaves(color = "#407FFF".toColorInt())
            }
        }
    }

    private fun onConnectDeviceClick(s: String) {
        binding.apply {
            imgBluetooth.setImageResource(R.drawable.ic_bluetooth_connected)
            clConnectedDevice.visible()
            grpAvailableDevices.gone()
            btnScanForDevices.gone()
            imgBluetooth.removeRippleWaves()
        }
    }

}