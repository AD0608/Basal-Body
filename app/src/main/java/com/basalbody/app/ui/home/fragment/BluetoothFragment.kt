package com.basalbody.app.ui.home.fragment

import android.Manifest
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.os.Build
import android.os.IBinder
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.graphics.toColorInt
import androidx.lifecycle.lifecycleScope
import com.basalbody.app.R
import com.basalbody.app.base.BaseFragment
import com.basalbody.app.databinding.FragmentBluetoothBinding
import com.basalbody.app.extensions.addRippleWaves
import com.basalbody.app.extensions.changeText
import com.basalbody.app.extensions.gone
import com.basalbody.app.extensions.startNewActivity
import com.basalbody.app.extensions.visible
import com.basalbody.app.service.BluetoothService
import com.basalbody.app.service.ConnectionState
import com.basalbody.app.ui.home.activity.ConnectedDeviceActivity
import com.basalbody.app.ui.home.adapter.BluetoothDevicesListAdapter
import com.basalbody.app.ui.home.viewmodel.HomeViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


const val BLUETOOTH_PERMISSION_REQUEST_CODE = 1001

@AndroidEntryPoint
class BluetoothFragment :
    BaseFragment<HomeViewModel, FragmentBluetoothBinding>(FragmentBluetoothBinding::inflate) {

    /** Service to handle all Bluetooth scanning and connection logic */
    private lateinit var bluetoothService: BluetoothService

    /** Tracks whether scanning is currently active */
    private var isScanning = false

    override fun getViewBinding(): FragmentBluetoothBinding =
        FragmentBluetoothBinding.inflate(layoutInflater)

    private val availableDevicesAdapter by lazy {
        BluetoothDevicesListAdapter(::onConnectDeviceClick)
    }

    override fun initSetup() {
        binding.apply {
            toolBar.tvTitle.changeText(getString(R.string.label_bluetooth))
            toolBar.ivBack.gone()
            rvAvailableDevices.adapter = availableDevicesAdapter
            tvLabelAvailableDevices.gone()
            rvAvailableDevices.gone()
            bluetoothService = BluetoothService(requireContext())
            observeBluetoothState()
            // Check and request necessary permissions
            if (!checkBluetoothPermissions()) {
                showPermissionRationaleDialog()
            }
        }
    }

    override fun listeners() {
        binding.apply {
            btnScanForDevices.setOnClickListener {
                imgBluetooth.addRippleWaves(color = "#46B74F".toColorInt())
                tvLabelAvailableDevices.visible()
                rvAvailableDevices.visible()
            }
        }
    }

    private fun onConnectDeviceClick(device: BluetoothService.BluetoothDeviceData) {
        if (checkBluetoothPermissions() && bluetoothService.isBluetoothSupportedAndEnabled()) {
            bluetoothService.connectToDevice(device.address)
            startNewActivity(ConnectedDeviceActivity::class.java)
        } else {
            Toast.makeText(requireContext(), "Permissions or Bluetooth needed.", Toast.LENGTH_SHORT)
                .show()
            startScanFlow()
        }
    }

    /**
     * ActivityResultLauncher to enable Bluetooth if it is disabled.
     * Triggers [startScanFlow] if Bluetooth is successfully enabled.
     */
    private val enableBluetoothLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            Log.d("MainActivity", "Bluetooth successfully enabled by user.")
            Toast.makeText(
                requireContext(),
                "Bluetooth enabled. Checking permissions...",
                Toast.LENGTH_SHORT
            ).show()
            startScanFlow()
        } else {
            Toast.makeText(
                requireContext(),
                "Bluetooth not enabled. Cannot scan for devices.",
                Toast.LENGTH_LONG
            ).show()
            isScanning = false
            updateScanButtonUI()
        }
    }

    /**
     * Checks Bluetooth permissions and status, requests enabling Bluetooth
     * if needed, then starts scanning for devices.
     */
    private fun startScanFlow() {
        if (!checkBluetoothPermissions()) {
            showPermissionRationaleDialog()
            return
        }

        if (!bluetoothService.isBluetoothSupportedAndEnabled()) {
            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            enableBluetoothLauncher.launch(enableBtIntent)
            return
        }

        bluetoothService.startScan()
        isScanning = true
        updateScanButtonUI()
        binding.imgBluetooth.addRippleWaves(color = "#46B74F".toColorInt())
    }

    /**
     * Observes [BluetoothService] device list and connection state.
     * Updates UI for available devices, connected device, and connection status.
     */
    private fun observeBluetoothState() {
        // Observe available devices
        lifecycleScope.launch {
            bluetoothService.availableDevices.collect { devices ->
                val connectedDevice = devices.find { it.isConnected }
                val availableForConnection = devices.filter { !it.isConnected }
                availableDevicesAdapter.submitList(availableForConnection)
                updateScanButtonUI()
            }
        }

        /*// NEW: Observe temperature and humidity data
        lifecycleScope.launch {
            bluetoothService.temperatureData.collect { data ->
                dataDisplayTextView.text = data
            }
        }*/
    }

    /**
     * Updates the scan button UI and scanning animation based on [isScanning].
     */
    private fun updateScanButtonUI() {
        if (isScanning) {
            binding.btnScanForDevices.text = "Stop Scan"
        } else {
            binding.btnScanForDevices.text = "Scan for Devices"
        }
    }

    /**
     * Checks if all required Bluetooth and location permissions are granted.
     *
     * @return True if all permissions are granted, false otherwise
     */
    private fun checkBluetoothPermissions(): Boolean {
        return when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
                val scan = ActivityCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.BLUETOOTH_SCAN
                ) == PackageManager.PERMISSION_GRANTED
                val connect = ActivityCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.BLUETOOTH_CONNECT
                ) == PackageManager.PERMISSION_GRANTED
                val fineLocation = ActivityCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED &&
                        ActivityCompat.checkSelfPermission(
                            requireContext(),
                            Manifest.permission.ACCESS_COARSE_LOCATION
                        ) == PackageManager.PERMISSION_GRANTED
                scan && connect && fineLocation
            }

            else -> {
                ActivityCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED &&
                        ActivityCompat.checkSelfPermission(
                            requireContext(),
                            Manifest.permission.ACCESS_COARSE_LOCATION
                        ) == PackageManager.PERMISSION_GRANTED
            }
        }
    }

    /**
     * Shows a dialog explaining why the app needs Bluetooth and location permissions.
     * On clicking "Allow", the actual permission request is triggered.
     */
    private fun showPermissionRationaleDialog() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Permissions Required")
        val message = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            "This app requires Bluetooth and Location permissions to scan and connect " +
                    "to nearby Bluetooth devices. Please allow these permissions to continue."
        } else {
            "This app requires Location permission to scan and connect to nearby Bluetooth devices. " +
                    "Please allow this permission to continue."
        }
        builder.setMessage(message)
        builder.setCancelable(false)

        // Action button to request permissions
        builder.setPositiveButton("Allow") { dialog, _ ->
            requestBluetoothPermissions()
            dialog.dismiss()
        }

        // Optional: cancel button
        builder.setNegativeButton("Cancel") { dialog, _ ->
            Toast.makeText(
                requireContext(),
                "Permissions denied. Cannot scan for devices.",
                Toast.LENGTH_LONG
            ).show()
            dialog.dismiss()
        }

        builder.show()
    }


    /**
     * Requests Bluetooth and location permissions at runtime if not already granted.
     */
    /**
     * Requests Bluetooth and location permissions with rationale and permanent denial handling.
     */
    private fun requestBluetoothPermissions() {
        val permissionsToRequest = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            arrayOf(
                Manifest.permission.BLUETOOTH_SCAN,
                Manifest.permission.BLUETOOTH_CONNECT,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        } else {
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        }

        if (!checkBluetoothPermissions()) {
            // Check if any permission was permanently denied
            val permanentlyDenied = permissionsToRequest.any { permission ->
                !ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(), permission) &&
                        ActivityCompat.checkSelfPermission(
                            requireContext(),
                            permission
                        ) != PackageManager.PERMISSION_GRANTED
            }

            if (permanentlyDenied) {
                // Show dialog to redirect user to app settings
                AlertDialog.Builder(requireContext())
                    .setTitle("Permissions Required")
                    .setMessage("Some permissions are permanently denied. Please enable them in app settings to use Bluetooth features.")
                    .setPositiveButton("Open Settings") { dialog, _ ->
                        val intent =
                            Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                        intent.data = android.net.Uri.fromParts("package", requireActivity().packageName, null)
                        startActivity(intent)
                        dialog.dismiss()
                    }
                    .setNegativeButton("Cancel") { dialog, _ -> dialog.dismiss() }
                    .show()
            } else {
                // Show normal rationale dialog
                val message = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    "This app requires Bluetooth and Location permissions to scan and connect to devices."
                } else {
                    "This app requires Location permissions to scan Bluetooth devices."
                }

                AlertDialog.Builder(requireContext())
                    .setTitle("Permissions Required")
                    .setMessage(message)
                    .setCancelable(false)
                    .setPositiveButton("Allow") { dialog, _ ->
                        ActivityCompat.requestPermissions(
                            requireActivity(),
                            permissionsToRequest,
                            BLUETOOTH_PERMISSION_REQUEST_CODE
                        )
                        dialog.dismiss()
                    }
                    .setNegativeButton("Cancel") { dialog, _ -> dialog.dismiss() }
                    .show()
            }
        }
    }

    /**
     * Handles the result of runtime permission requests.
     *
     * @param requestCode The request code passed in requestPermissions()
     * @param permissions The requested permissions
     * @param grantResults The results for the corresponding permissions
     * @param deviceId Deprecated parameter, not used
     */
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String?>,
        grantResults: IntArray,
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == BLUETOOTH_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                Toast.makeText(requireContext(), "Permissions granted. Starting scan...", Toast.LENGTH_SHORT)
                    .show()
                startScanFlow()
            } else {
                // Check if any permission was permanently denied
                val permanentlyDenied = permissions.indices.any { index ->
                    ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(), permissions[index]!!)
                        .not()
                }

                if (permanentlyDenied) {
                    // Permission permanently denied
                    AlertDialog.Builder(requireContext())
                        .setTitle("Permissions Required")
                        .setMessage(
                            "Permissions are permanently denied. Please enable them in App Settings to scan Bluetooth devices."
                        )
                        .setPositiveButton("Open Settings") { dialog, _ ->
                            val intent =
                                Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                            intent.data = android.net.Uri.fromParts("package", requireActivity().packageName, null)
                            startActivity(intent)
                            dialog.dismiss()
                        }
                        .setNegativeButton("Cancel") { dialog, _ -> dialog.dismiss() }
                        .show()
                } else {
                    // Temporary denial, show rationale dialog again
                    Toast.makeText(
                        requireContext(),
                        "Permissions denied. Cannot scan for devices.",
                        Toast.LENGTH_LONG
                    ).show()
                    showPermissionRationaleDialog()
                }
            }
        }
    }
}