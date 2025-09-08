package com.basalbody.app.ui.profile.activity

import android.util.Log
import com.basalbody.app.R
import com.basalbody.app.base.BaseActivity
import com.basalbody.app.databinding.ActivityConnectedBluetoothDeviceBinding
import com.basalbody.app.extensions.changeText
import com.basalbody.app.extensions.onSafeClick
import com.basalbody.app.ui.profile.adapter.ConnectedBluetoothDevicesListAdapter
import com.basalbody.app.ui.profile.viewmodel.ProfileViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ConnectedBluetoothDeviceActivity :
    BaseActivity<ProfileViewModel, ActivityConnectedBluetoothDeviceBinding>() {

    private val TAG = "ConnectedBluetoothDeviceActivity"

    override fun getViewBinding(): ActivityConnectedBluetoothDeviceBinding =
        ActivityConnectedBluetoothDeviceBinding.inflate(layoutInflater)

    private val connectedDevicesAdapter by lazy {
        ConnectedBluetoothDevicesListAdapter(
            arrayListOf("", "", "", "", "", "","", "", "", "", "", "","", "", "", "", "", "","", "", "", "", "", ""),
            ::onConnectDeviceClick
        )
    }

    override fun initSetup() {
        setupUI()
    }

    private fun setupUI() {
        binding.apply {
            llToolBar.tvTitle.changeText(R.string.label_connect_bluetooth_devices)
            rvConnectedDevices.adapter = connectedDevicesAdapter
        }
    }

    override fun listeners() {
        binding.apply {
            llToolBar.ivBack.onSafeClick {
                finish()
            }
        }
    }

    private fun onConnectDeviceClick(s: String) {
        Log.e(TAG, "onConnectDeviceClick()")
    }
}