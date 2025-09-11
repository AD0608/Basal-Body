package com.basalbody.app.ui.home.activity

import android.os.Handler
import android.os.Looper
import androidx.core.graphics.toColorInt
import com.basalbody.app.R
import com.basalbody.app.base.BaseActivity
import com.basalbody.app.databinding.ActivityConnectedDeviceBinding
import com.basalbody.app.extensions.addRippleWaves
import com.basalbody.app.extensions.changeBackgroundTint
import com.basalbody.app.extensions.changeText
import com.basalbody.app.extensions.gone
import com.basalbody.app.extensions.onSafeClick
import com.basalbody.app.extensions.removeButtonTint
import com.basalbody.app.extensions.removeDrawableEnd
import com.basalbody.app.extensions.setDrawableEndWithColor
import com.basalbody.app.extensions.visible
import com.basalbody.app.ui.home.adapter.BluetoothDevicesListAdapter
import com.basalbody.app.ui.home.dialog.DeviceDisconnectedDialog
import com.basalbody.app.ui.home.viewmodel.HomeViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ConnectedDeviceActivity : BaseActivity<HomeViewModel, ActivityConnectedDeviceBinding>() {
    override fun getViewBinding(): ActivityConnectedDeviceBinding =
        ActivityConnectedDeviceBinding.inflate(layoutInflater)

    private val availableDevicesAdapter by lazy {
        BluetoothDevicesListAdapter(arrayListOf("", ""), ::onConnectDeviceClick)
    }

    override fun initSetup() {
        binding.apply {
            imgBluetooth.addRippleWaves(color = "#46B74F".toColorInt())
            rvFoundedDevices.adapter = availableDevicesAdapter
            toolBar.tvTitle.changeText(getString(R.string.label_connected_device))
            setInitialUI()
        }
    }

    private fun setInitialUI() {
        binding.apply {
            btnScanForDevices.changeText("Scanning...")
            btnScanForDevices.removeButtonTint()
            btnScanForDevices.removeDrawableEnd()
            rvFoundedDevices.gone()
            tvLabelFoundedDevices.visible()
            clConnectedDevice.gone()
            cvNoDevicesFound.visible()

            // after 3 seconds show the devices list
            Handler(Looper.getMainLooper()).postDelayed({
                cvNoDevicesFound.gone()
                clConnectedDevice.gone()
                tvLabelFoundedDevices.visible()
                rvFoundedDevices.visible()
                btnScanForDevices.changeText(getString(R.string.btn_scan_for_devices))
                btnScanForDevices.changeBackgroundTint(R.color.black)
                btnScanForDevices.setDrawableEndWithColor(R.drawable.ic_button_icon, R.color.white)
            }, 3000)
        }
    }

    override fun listeners() {
        binding.apply {
            toolBar.ivBack onSafeClick {
                onBackPressedDispatcher.onBackPressed()
            }

            btnChangeConnectionStatus onSafeClick {
                DeviceDisconnectedDialog.newInstance(isCancel = true, root, this@ConnectedDeviceActivity).show(
                    supportFragmentManager, DeviceDisconnectedDialog::class.java.name
                )
            }

            btnScanForDevices onSafeClick {
                if (btnScanForDevices.text == getString(R.string.btn_scan_for_devices)) setInitialUI()
            }
        }
    }

    private fun onConnectDeviceClick(s: String) {
        binding.apply {
            cvNoDevicesFound.gone()
            rvFoundedDevices.gone()
            clConnectedDevice.visible()
            tvLabelFoundedDevices.gone()
        }
    }
}