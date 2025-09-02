package com.basalbody.app.ui.home.fragment

import com.basalbody.app.base.BaseFragment
import com.basalbody.app.databinding.FragmentBluetoothBinding
import com.basalbody.app.ui.home.viewmodel.HomeViewModel

class BluetoothFragment :
    BaseFragment<HomeViewModel, FragmentBluetoothBinding>(FragmentBluetoothBinding::inflate) {
    override fun getViewBinding(): FragmentBluetoothBinding =
        FragmentBluetoothBinding.inflate(layoutInflater)

    override fun initSetup() {

    }

    override fun listeners() {

    }

}