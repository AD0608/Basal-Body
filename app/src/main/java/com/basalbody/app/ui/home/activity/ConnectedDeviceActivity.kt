package com.basalbody.app.ui.home.activity

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.graphics.toColorInt
import androidx.lifecycle.lifecycleScope
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
import com.basalbody.app.extensions.setButtonTint
import com.basalbody.app.extensions.setDrawableEndWithColor
import com.basalbody.app.extensions.startNewActivity
import com.basalbody.app.extensions.visible
import com.basalbody.app.service.BluetoothService
import com.basalbody.app.service.ConnectionState
import com.basalbody.app.ui.home.adapter.BluetoothDevicesListAdapter
import com.basalbody.app.ui.home.dialog.DeviceDisconnectedDialog
import com.basalbody.app.ui.home.viewmodel.HomeViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ConnectedDeviceActivity : BaseActivity<HomeViewModel, ActivityConnectedDeviceBinding>() {
    override fun getViewBinding(): ActivityConnectedDeviceBinding =
        ActivityConnectedDeviceBinding.inflate(layoutInflater)

    private lateinit var bluetoothService: BluetoothService

    private var isScanning = false
    private val availableDevicesAdapter by lazy {
        BluetoothDevicesListAdapter(::onConnectDeviceClick)
    }

    private var connectedDevice : BluetoothService.BluetoothDeviceData? = null

    override fun addObservers() {
        lifecycleScope.launch {
            bluetoothService.availableDevices.collect { devices ->
                val connectedDevice = devices.find { it.isConnected }
                val availableForConnection = devices.filter { !it.isConnected }
                availableDevicesAdapter.submitList(availableForConnection)
                updateConnectedDeviceUI(connectedDevice)
                updateScanButtonUI()
            }
        }

        // Observe connection state
        lifecycleScope.launch {
            bluetoothService.connectionState.collect { state ->
                when (state) {
                    ConnectionState.CONNECTING -> {
                        binding.rvFoundedDevices.visibility = View.GONE
                        binding.clConnectedDevice.visible()
                    }

                    ConnectionState.CONNECTED -> {
                        binding.rvFoundedDevices.visibility = View.GONE
                        bluetoothService.stopScan()
                        binding.clConnectedDevice.visible()
                        isScanning = false
                        updateScanButtonUI()
                        updateConnectedDeviceUI(connectedDevice)
                    }

                    ConnectionState.DISCONNECTING -> {
                        updateScanButtonUI()
                    }

                    ConnectionState.DISCONNECTED -> {
                        binding.apply {
                            rvFoundedDevices.visibility = View.VISIBLE
                            clConnectedDevice.gone()
                            tvLabelFoundedDevices.visible()
                        }
                        isScanning = false
                        updateScanButtonUI()
                        connectedDevice = null
                        // Optionally restart scanning after disconnect
                        if (!isScanning) {
                            setInitialUI()
                        }
                    }

                    ConnectionState.ERROR -> {
                        binding.apply {
                            clConnectedDevice.gone()
                            cvNoDevicesFound.visible()
                            rvFoundedDevices.visible()
                            tvLabelFoundedDevices.visible()
                        }
                        isScanning = false
                        updateScanButtonUI()
                        Toast.makeText(
                            this@ConnectedDeviceActivity,
                            "Connection error. Please try again.",
                            Toast.LENGTH_SHORT
                        ).show()
                        connectedDevice = null
                    }
                }
            }
        }
    }

    override fun initSetup() {
        binding.apply {
            imgBluetooth.addRippleWaves(color = "#46B74F".toColorInt())
            rvFoundedDevices.adapter = availableDevicesAdapter
            toolBar.tvTitle.changeText(getString(R.string.label_connected_device))
            bluetoothService = BluetoothService(this@ConnectedDeviceActivity)
            // Check current connection state before setting UI
            checkInitialConnectionState()
        }
    }

    private fun checkInitialConnectionState() {
        when (bluetoothService.connectionState.value) {
            ConnectionState.CONNECTED, ConnectionState.CONNECTING -> {
                // Device is already connected or connecting, show connected UI
                val connected = bluetoothService.availableDevices.value.find { it.isConnected }
                connectedDevice = connected
                binding.apply {
                    rvFoundedDevices.gone()
                    tvLabelFoundedDevices.gone()
                    clConnectedDevice.visible()
                    cvNoDevicesFound.gone()
                    updateConnectedDeviceUI(connectedDevice)
                }
                isScanning = false
                updateScanButtonUI()
            }
            ConnectionState.DISCONNECTED, ConnectionState.ERROR -> {
                // No connection, start scanning
                setInitialUI()
            }
            ConnectionState.DISCONNECTING -> {
                // Wait for disconnection to complete
                binding.apply {
                    rvFoundedDevices.visible()
                    tvLabelFoundedDevices.visible()
                    clConnectedDevice.gone()
                    cvNoDevicesFound.gone()
                }
                isScanning = false
                updateScanButtonUI()
            }
        }
    }

    private fun setInitialUI() {
        binding.apply {
            rvFoundedDevices.visible()
            tvLabelFoundedDevices.visible()
            clConnectedDevice.gone()
            cvNoDevicesFound.gone()
            bluetoothService.startScan()
            isScanning = true
            updateScanButtonUI()
        }
    }

    override fun listeners() {
        binding.apply {
            toolBar.ivBack onSafeClick {
                onBackPressedDispatcher.onBackPressed()
            }

            btnChangeConnectionStatus onSafeClick {
                bluetoothService.disconnectDevice()
                connectedDevice = null
                binding.apply {
                    cvNoDevicesFound.visible()
                    rvFoundedDevices.gone()
                    clConnectedDevice.gone()
                    tvLabelFoundedDevices.visible()
                }
                /*DeviceDisconnectedDialog.newInstance(isCancel = true, root, this@ConnectedDeviceActivity).show(
                    supportFragmentManager, DeviceDisconnectedDialog::class.java.name
                )*/
            }

            btnScanForDevices onSafeClick {
                if (btnScanForDevices.text == getString(R.string.btn_scan_for_devices)) setInitialUI()
            }
        }
    }

    private fun onConnectDeviceClick(device: BluetoothService.BluetoothDeviceData) {
        connectedDevice = device
        bluetoothService.connectToDevice(device.address)
        binding.apply {
            cvNoDevicesFound.gone()
            rvFoundedDevices.gone()
            clConnectedDevice.visible()
            tvLabelFoundedDevices.gone()
        }
    }

    private fun updateScanButtonUI() {
        binding.apply {
            if (isScanning) {
                btnScanForDevices.text = "Scanning..."
                btnScanForDevices.removeButtonTint()
                btnScanForDevices.removeDrawableEnd()
            } else {
                btnScanForDevices.text = "Scan for Devices"
                btnScanForDevices.setButtonTint(R.color.color070707)
                btnScanForDevices.setDrawableEndWithColor(R.drawable.ic_button_icon, R.color.white)
            }
        }
    }

    private fun updateConnectedDeviceUI(device: BluetoothService.BluetoothDeviceData?) {
        if (device != null) {
            binding.clConnectedDevice.visibility = View.VISIBLE
            binding.tvConnectedDeviceName.text = device.name
            binding.tvConnectedDeviceType.text = device.address
            binding.rvFoundedDevices.visibility = View.GONE
        } else {
            if (bluetoothService.connectionState.value == ConnectionState.DISCONNECTED) {
                binding.rvFoundedDevices.visibility = View.VISIBLE
            }
            binding.clConnectedDevice.visibility = View.GONE
        }
    }
}