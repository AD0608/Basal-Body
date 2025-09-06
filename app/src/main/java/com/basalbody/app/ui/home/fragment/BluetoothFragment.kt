package com.basalbody.app.ui.home.fragment

import androidx.core.graphics.toColorInt
import com.basalbody.app.R
import com.basalbody.app.base.BaseFragment
import com.basalbody.app.databinding.FragmentBluetoothBinding
import com.basalbody.app.extensions.addRippleWaves
import com.basalbody.app.extensions.changeText
import com.basalbody.app.extensions.gone
import com.basalbody.app.extensions.startNewActivity
import com.basalbody.app.extensions.visible
import com.basalbody.app.ui.home.activity.ConnectedDeviceActivity
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
            toolBar.tvTitle.changeText(getString(R.string.label_bluetooth))
            toolBar.ivBack.gone()
            imgBluetooth.setImageResource(R.drawable.ic_bluetooth_blue)
            rvAvailableDevices.adapter = availableDevicesAdapter
            tvLabelAvailableDevices.gone()
            rvAvailableDevices.gone()
        }
    }

    override fun listeners() {
        binding.apply {
            btnScanForDevices.setOnClickListener {
                imgBluetooth.addRippleWaves(color = "#407FFF".toColorInt())
                tvLabelAvailableDevices.visible()
                rvAvailableDevices.visible()
            }
        }
    }

    private fun onConnectDeviceClick(s: String) {
        startNewActivity(ConnectedDeviceActivity::class.java)
    }

}