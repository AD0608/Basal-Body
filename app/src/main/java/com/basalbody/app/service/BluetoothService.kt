package com.basalbody.app.service

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattDescriptor
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothProfile
import android.bluetooth.le.BluetoothLeScanner
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import com.basalbody.app.utils.BleUuids
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import java.util.concurrent.ConcurrentHashMap

/**
 * Service class for scanning, connecting, and managing Bluetooth LE devices.
 * Handles BLE scanning, GATT connection, and device state management.
 */
@SuppressLint("MissingPermission")
class BluetoothService(private val context: Context) {

    /** Android BluetoothManager for accessing BluetoothAdapter and profiles */
    private val bluetoothManager: BluetoothManager =
        context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager

    /** Main Bluetooth adapter */
    private val bluetoothAdapter: BluetoothAdapter? = bluetoothManager.adapter

    /** Bluetooth LE scanner for performing device scans */
    private val bluetoothLeScanner: BluetoothLeScanner? = bluetoothAdapter?.bluetoothLeScanner

    /** Handler for managing scan timeout and posting runnables */
    private val scanHandler = Handler(Looper.getMainLooper())

    /** Currently active GATT connection */
    private var bluetoothGatt: BluetoothGatt? = null

    private var writeCharacteristic: BluetoothGattCharacteristic? = null
    private var readCharacteristic: BluetoothGattCharacteristic? = null

    /** StateFlow holding the latest temperature and humidity data */
    private val _temperatureData = MutableStateFlow<String>("N/A: Not Connected")
    val temperatureData: StateFlow<String> = _temperatureData

    /**
     * Data class representing a Bluetooth device.
     *
     * @property address MAC address of the device
     * @property name Display name of the device
     * @property isConnected Whether this device is currently connected
     * @property isScanning Whether this device is currently being scanned
     */
    data class BluetoothDeviceData(
        val address: String,
        val name: String?,
        val isConnected: Boolean = false,
        val isScanning: Boolean = false
    )

    /** StateFlow holding the list of available devices found during scan */
    private val _availableDevices = MutableStateFlow<List<BluetoothDeviceData>>(emptyList())

    /** Public read-only StateFlow for observing available devices */
    val availableDevices: StateFlow<List<BluetoothDeviceData>> = _availableDevices

    /** StateFlow holding the current connection state of the selected device */
    private val _connectionState = MutableStateFlow(ConnectionState.DISCONNECTED)

    /** Public read-only StateFlow for observing the connection state */
    val connectionState: StateFlow<ConnectionState> = _connectionState

    /** Map to track discovered devices by MAC address */
    private val discoveredDevices = ConcurrentHashMap<String, BluetoothDeviceData>()

    /** Runnable to stop scanning after a timeout */
    private val stopScanRunnable = Runnable { stopScan() }

    init {
        // Initialize available devices list from discovered devices map
        _availableDevices.value = discoveredDevices.values.toList()
    }

    /**
     * Checks if Bluetooth is supported and currently enabled.
     *
     * @return True if Bluetooth is available and enabled, false otherwise
     */
    fun isBluetoothSupportedAndEnabled(): Boolean {
        return bluetoothAdapter?.isEnabled == true
    }

    /**
     * Callback for receiving BLE scan results.
     * Updates the list of discovered devices and notifies observers.
     */
    private val leScanCallback: ScanCallback = object : ScanCallback() {
        override fun onScanResult(callbackType: Int, result: ScanResult) {
            super.onScanResult(callbackType, result)

            val device = result.device
            if (device.address.isNullOrEmpty()) return

            val deviceData = BluetoothDeviceData(
                address = device.address,
                name = device.name ?: result.scanRecord?.deviceName ?: "N/A"
            )

            if (!discoveredDevices.containsKey(device.address)) {
                discoveredDevices[device.address] = deviceData
                _availableDevices.update {
                    discoveredDevices.values.toList().filter { it.name != "N/A" }
                }
                Log.d("BluetoothService", "Found device: ${device.name} (${device.address})")
            }
        }

        override fun onScanFailed(errorCode: Int) {
            Log.e("BluetoothService", "Scan failed with error code: $errorCode")
            stopScan()
        }
    }

    /**
     * Starts scanning for BLE devices.
     * Stops any previous scan, clears existing devices, and schedules scan stop.
     */
    fun startScan() {
        if (!isBluetoothSupportedAndEnabled()) {
            Log.w("BluetoothService", "Bluetooth not enabled or supported.")
            return
        }
        if (bluetoothLeScanner == null) {
            Log.e("BluetoothService", "Bluetooth LE Scanner is unavailable.")
            return
        }

        stopScan()
        discoveredDevices.clear()
        _availableDevices.value = emptyList()

        Log.d("BluetoothService", "Starting real BLE device scan.")
        bluetoothLeScanner.startScan(leScanCallback)

        // Stops scan automatically after 100 seconds
        scanHandler.postDelayed(stopScanRunnable, 100000)
    }

    /**
     * Stops the current BLE scan and removes scan timeout callbacks.
     */
    fun stopScan() {
        if (bluetoothAdapter?.isEnabled == true && bluetoothLeScanner != null) {
            Log.d("BluetoothService", "Stopping BLE device scan.")
            bluetoothLeScanner.stopScan(leScanCallback)
        }
        scanHandler.removeCallbacks(stopScanRunnable)
    }

    /**
     * Connects to a BLE device by its MAC address.
     *
     * @param address MAC address of the device to connect
     */
    fun connectToDevice(address: String) {
        if (_connectionState.value != ConnectionState.DISCONNECTED) {
            Log.w("BluetoothService", "Already in a connection process or connected.")
            return
        }

        val device: BluetoothDevice = try {
            bluetoothAdapter?.getRemoteDevice(address)
                ?: throw Exception("Bluetooth adapter or device not found.")
        } catch (e: Exception) {
            Log.e("BluetoothService", "Invalid address or Bluetooth not initialized: ${e.message}")
            _connectionState.value = ConnectionState.ERROR
            return
        }

        stopScan()

        Log.d("BluetoothService", "Attempting to connect to ${device.name} (${device.address})")
        _connectionState.value = ConnectionState.CONNECTING

        bluetoothGatt = device.connectGatt(context, false, gattCallback, BluetoothDevice.TRANSPORT_LE)

        if (bluetoothGatt == null) {
            Log.e("BluetoothService", "GATT connection failed to initialize.")
            _connectionState.value = ConnectionState.ERROR
        }
    }

    /**
     * Disconnects from the currently connected BLE device.
     * Updates connection state accordingly.
     */
    fun disconnectDevice() {
        val gatt = bluetoothGatt
        if (gatt == null) {
            Log.w("BluetoothService", "BluetoothGatt not initialized.")
            _connectionState.value = ConnectionState.DISCONNECTED
            return
        }

        Log.d("BluetoothService", "Attempting to disconnect.")
        _connectionState.value = ConnectionState.DISCONNECTING

        gatt.disconnect()
    }

    /**
     * Updates the connection status for a specific device in the available devices list.
     *
     * @param address MAC address of the device
     * @param isConnected True if device is connected, false otherwise
     */
    private fun updateDeviceListConnectionState(address: String, isConnected: Boolean) {
        _availableDevices.update { currentList ->
            currentList.map { device ->
                if (device.address == address) {
                    device.copy(isConnected = isConnected)
                } else {
                    device.copy(isConnected = false)
                }
            }
        }
    }

    /**
     * Finds and enables the necessary characteristics (0xFFF1 for Read/Notify, 0xFFF2 for Write).
     */
    // In BluetoothService.kt

    private fun setupCommunication(gatt: BluetoothGatt) {
        val service = gatt.getService(BleUuids.CUSTOM_SERVICE_UUID)

        if (service == null) {
            Log.e("BluetoothService", "Custom service UUID not found: ${BleUuids.CUSTOM_SERVICE_UUID}")

            // ... (Keep the CRITICAL DEBUGGING LOG BLOCK here for future diagnostics) ...
            Log.w("BluetoothService", "--- Listing All Discovered Services for Debugging ---")
            gatt.services.forEach { s ->
                Log.w("BluetoothService", "Service Found: ${s.uuid}")
                s.characteristics.forEach { c ->
                    Log.w("BluetoothService", "  -> Characteristic: ${c.uuid} (Props: ${c.properties})")
                }
            }
            Log.w("BluetoothService", "------------------------------------------------------")

            _connectionState.value = ConnectionState.ERROR
            return
        }

        // Use the swapped characteristic assignments as determined by the log analysis
        readCharacteristic = service.getCharacteristic(BleUuids.READ_HOST_DATA_CHAR_UUID) // FFF2 UUID
        writeCharacteristic = service.getCharacteristic(BleUuids.WRITE_HOST_DATA_CHAR_UUID) // FFF1 UUID

        if (readCharacteristic == null || writeCharacteristic == null) {
            Log.e("BluetoothService", "Read(FFF2) or Write(FFF1) characteristic not found.")
            _connectionState.value = ConnectionState.ERROR
            return
        }

        // Manually initiate data request since onDescriptorWrite will never be called.
        readCurrentInfo()
    }

    /**
     * Writes the command to the device to request the current info (0x10).
     */
    // In BluetoothService.kt, inside the readCurrentInfo() function

    fun readCurrentInfo() {
        val gatt = bluetoothGatt
        val characteristic = writeCharacteristic
        if (gatt == null || characteristic == null) {
            return
        }

        characteristic.value = BleUuids.READ_INFO_COMMAND

        // 💡 FIX 1: Change writeType from DEFAULT (8) to NO_RESPONSE (4)
        characteristic.writeType = BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE
    }


    /**
     * Processes the 11-byte response from the device (Section A: Read Current Information).
     * Response structure (Bytes 0-10): [CMD (0x10)] [Yr] [Mo] [Dt] [Hr] [Min] [Sec] [HUMI_H] [HUMI_L] [TEMP] [C/F]
     */
    private fun processTemperatureHumidity(data: ByteArray) {
        // The document lists 11 bytes of data (Byte 0 to Byte 10)
        if (data.size < 11) {
            Log.e("BluetoothService", "Data length too short (${data.size}), expected 11 bytes.")
            _temperatureData.value = "ERROR: Bad data length."
            return
        }

        // Temperature (TEMP) is at Byte 9 (10th byte)
        val rawTemp = data[9].toInt() and 0xFF // Read as unsigned byte

        // Unit (C/F) is at Byte 10 (11th byte)
        val unitByte = data[10].toInt()
        val isFahrenheit = (unitByte == 1) // 0 = Celsius, 1 = Fahrenheit

        // Humidity (HUMI) is combined from Byte 7 (H) and Byte 8 (L)
        // Note: The doc formula for HUMI seems incorrect for the byte order,
        // using the standard BLE method: (High Byte << 8) + Low Byte
        val rawHumiH = data[7].toInt() and 0xFF
        val rawHumiL = data[8].toInt() and 0xFF
        val rawHumi = (rawHumiH shl 8) + rawHumiL

        // --- Temperature Conversion ---
        val temperature: Float
        val tempUnit: String

        // Protocol specifies: C = TEMP / 100 or F = ((TEMP * 18) / 10) + 3200
        // We'll follow the document exactly for C/F units:
        if (isFahrenheit) {
            // F = ((TEMP * 18) / 10) + 3200 (Assuming rawTemp is in 0.01 increments)
            // This formula in the document seems highly unusual, but we implement it as written.
            temperature = (((rawTemp * 18f) / 10f) + 3200f) / 100f
            tempUnit = "°F"
        } else {
            // C = TEMP / 100
            temperature = rawTemp.toFloat() / 100f
            tempUnit = "°C"
        }

        // --- Humidity Conversion (Assuming similar 0.01 increments) ---
        // Assuming rawHumi value represents Humidity * 100
        val humidity = rawHumi.toFloat() / 100f

        val displayData = String.format(
                "Time: %d/%02d/%02d %02d:%02d:%02d\nTemperature: %.2f %s\nHumidity: %.2f %%",
        // Use correct indices from the table:
        (data[1].toInt() and 0xFF) + 2000, // Year (Byte 1)
        data[2].toInt() and 0xFF,          // Month (Byte 2)
        data[3].toInt() and 0xFF,          // Date (Byte 3)
        data[4].toInt() and 0xFF,          // Hour (Byte 4)
        data[5].toInt() and 0xFF,          // Minute (Byte 5)
        data[6].toInt() and 0xFF,          // Second (Byte 6)

        temperature,
        tempUnit,
        humidity
        )

        Log.i("BluetoothService", "Parsed Data: $displayData")
        _temperatureData.value = displayData
    }


    /**
     * GATT callback to handle BLE connection, disconnection, and service discovery.
     */
    private val gattCallback = object : BluetoothGattCallback() {
        override fun onConnectionStateChange(gatt: BluetoothGatt, status: Int, newState: Int) {
            val deviceAddress = gatt.device.address
            Log.i("BluetoothService", "onConnectionStateChange: Status=$status, NewState=$newState")

            if (status == BluetoothGatt.GATT_SUCCESS) {
                when (newState) {
                    BluetoothProfile.STATE_CONNECTED -> {
                        Log.i("BluetoothService", "Device connected: $deviceAddress")
                        _connectionState.value = ConnectionState.CONNECTED
                        updateDeviceListConnectionState(deviceAddress, true)
                        //gatt.discoverServices()
                    }

                    BluetoothProfile.STATE_DISCONNECTED -> {
                        Log.i("BluetoothService", "Device disconnected: $deviceAddress")
                        gatt.close()
                        bluetoothGatt = null
                        _connectionState.value = ConnectionState.DISCONNECTED
                        updateDeviceListConnectionState(deviceAddress, false)

                        Log.d("BluetoothService", "Restarting BLE scan after disconnect.")
                        startScan()
                    }
                }
            } else {
                Log.e(
                    "BluetoothService", "GATT connection error with status $status. Disconnecting."
                )
                gatt.close()
                bluetoothGatt = null
                _connectionState.value = ConnectionState.ERROR
                updateDeviceListConnectionState(deviceAddress, false)

                startScan()
            }
        }

        override fun onServicesDiscovered(gatt: BluetoothGatt, status: Int) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                Log.i(
                    "BluetoothService",
                    "Services discovered for ${gatt.device.address}. Ready for communication."
                )
                setupCommunication(gatt)
            } else {
                Log.w("BluetoothService", "onServicesDiscovered received: $status")
            }
        }

        // NEW: Handles the successful enabling of notifications.
        override fun onDescriptorWrite(gatt: BluetoothGatt, descriptor: BluetoothGattDescriptor, status: Int) {
            super.onDescriptorWrite(gatt, descriptor, status)
            if (status == BluetoothGatt.GATT_SUCCESS) {
                Log.i("BluetoothService", "CCC descriptor written successfully. Requesting data.")
                // Notifications are now enabled. Request the data immediately.
                readCurrentInfo()
            }
        }


        // NEW: This callback handles data sent from the peripheral (the device) via notification
        @Deprecated("Deprecated in API 33") // Use the new method signature for modern Android
        override fun onCharacteristicChanged(
            gatt: BluetoothGatt,
            characteristic: BluetoothGattCharacteristic
        ) {
            @Suppress("DEPRECATION")
            val value = characteristic.value
            onCharacteristicChanged(gatt, characteristic, value)
        }

        override fun onCharacteristicWrite(
            gatt: BluetoothGatt,
            characteristic: BluetoothGattCharacteristic,
            status: Int
        ) {
            super.onCharacteristicWrite(gatt, characteristic, status)
            if (status == BluetoothGatt.GATT_SUCCESS) {
                // This confirms the READ_INFO_COMMAND (0x10) was successfully sent.
                // You can add further logging or command queuing logic here if needed.
                Log.d("BluetoothService", "Characteristic write successful: ${characteristic.uuid}")
            } else {
                Log.e("BluetoothService", "Characteristic write failed with status: $status")
            }
        }

        // NEW: Modern callback for receiving notification data
        override fun onCharacteristicChanged(
            gatt: BluetoothGatt,
            characteristic: BluetoothGattCharacteristic,
            value: ByteArray
        ) {
            super.onCharacteristicChanged(gatt, characteristic, value)

            // 1. Check if the notification is from the correct characteristic (0xFFF1)
            if (characteristic.uuid == BleUuids.READ_HOST_DATA_CHAR_UUID) {
                // 2. Check if the response command code matches (Byte 0 = 0x10)
                if (value.isNotEmpty() && value[0] == BleUuids.CMD_READ_INFO_RESPONSE) {
                    processTemperatureHumidity(value)
                } else {
                    Log.w("BluetoothService", "Received notification with unexpected command code or length.")
                }
            }
        }
    }

    /**
     * Simulates a battery error by disconnecting the device and setting connection state to ERROR.
     * After 5 seconds, the state transitions back to DISCONNECTED.
     */
    fun simulateBatteryError() {
        if (_connectionState.value == ConnectionState.CONNECTED) {
            _connectionState.value = ConnectionState.ERROR
            val connectedAddress = _availableDevices.value.firstOrNull { it.isConnected }?.address
            if (connectedAddress != null) {
                bluetoothGatt?.close()
                bluetoothGatt = null
                updateDeviceListConnectionState(connectedAddress, false)
            }
            Thread {
                Thread.sleep(5000)
                _connectionState.value = ConnectionState.DISCONNECTED
            }.start()
        }
    }
}

/**
 * Enum representing the current Bluetooth connection state.
 */
enum class ConnectionState {
    DISCONNECTED, CONNECTING, CONNECTED, DISCONNECTING, ERROR
}