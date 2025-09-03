package com.basalbody.app.ui.home.fragment

import androidx.core.graphics.toColorInt
import com.basalbody.app.base.BaseFragment
import com.basalbody.app.databinding.FragmentBluetoothBinding
import com.basalbody.app.extensions.addRippleWaves
import com.basalbody.app.ui.home.adapter.BluetoothDevicesListAdapter
import com.basalbody.app.ui.home.viewmodel.HomeViewModel

class BluetoothFragment :
    BaseFragment<HomeViewModel, FragmentBluetoothBinding>(FragmentBluetoothBinding::inflate) {
    override fun getViewBinding(): FragmentBluetoothBinding =
        FragmentBluetoothBinding.inflate(layoutInflater)

    private val availableDevicesAdapter by lazy {
        BluetoothDevicesListAdapter(arrayListOf("","",""))
    }

    override fun initSetup() {
        binding.imgBluetooth.addRippleWaves(color = "#407FFF".toColorInt())
        binding.apply {
            rvAvailableDevices.adapter = availableDevicesAdapter
        }
    }

    override fun listeners() {

    }

}