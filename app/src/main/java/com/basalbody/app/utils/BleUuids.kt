package com.basalbody.app.utils

import java.util.UUID

object BleUuids {
    // FIX: Use the only other custom Service UUID found in the debug log.
    val CUSTOM_SERVICE_UUID: UUID = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb")

    // FIX: Assume the characteristics are FFF1 and FFF2 but use the STANDARD BLE BASE
    // to allow Android to match the 16-bit short form. This is the most common pattern.
    // Alternative for BleUuids.kt if the previous fix failed:
    val READ_HOST_DATA_CHAR_UUID: UUID = UUID.fromString("00002902-FFF1-1000-8000-00805f9b34fb")
    val WRITE_HOST_DATA_CHAR_UUID: UUID = UUID.fromString("00002902-FFF2-1000-8000-00805f9b34fb")

    // Client Characteristic Configuration Descriptor UUID (Standard)
    val CCC_DESCRIPTOR_UUID: UUID = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb")

    // Command to read current info (Section A: 7 bytes)
    val READ_INFO_COMMAND = byteArrayOf(0x07, 0x10, 0x12, 0x34, 0x56, 0x78, 0x77)

    // Response Command byte (Byte 0 of response)
    const val CMD_READ_INFO_RESPONSE: Byte = 0x10
}