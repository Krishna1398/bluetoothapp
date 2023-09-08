package com.example.bluetoothapp

import android.Manifest
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.Context
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.core.app.ActivityCompat

class BluetoothHelper (private val context: Context){
    private val bluetoothAdapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()
    val bluetoothPermission = android.Manifest.permission.BLUETOOTH_CONNECT



    fun isBluetoothSupported(): Boolean {
        return bluetoothAdapter != null
    }

    fun isBluetoothEnabled(): Boolean {
        return bluetoothAdapter?.isEnabled ?: false
    }

    fun enableBluetooth() {


        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.BLUETOOTH_CONNECT
            ) != PackageManager.PERMISSION_GRANTED
        ) {

            return
        }
        bluetoothAdapter?.enable()
    }

    fun disableBluetooth() {
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.BLUETOOTH_CONNECT
            ) != PackageManager.PERMISSION_GRANTED
        ) {

            return
        }
        bluetoothAdapter?.disable()
    }

    fun getPairedDevices(): Set<BluetoothDevice>? {

        // Check for Bluetooth permission
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.BLUETOOTH_CONNECT
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // You can't access Bluetooth without permission, so request it here
            // You should handle the result in onRequestPermissionsResult
            ActivityCompat.requestPermissions(
                context as Activity, // Make sure context is an Activity
                arrayOf(Manifest.permission.BLUETOOTH_CONNECT),
                123
            )
            // Return an empty set or null for now
            return null
        }

        // Check if BluetoothAdapter is null
        if (bluetoothAdapter == null) {
            // Bluetooth is not supported on this device
            // Handle this case gracefully, e.g., show an error message
            return null
        }

        // Access paired devices
        return bluetoothAdapter.bondedDevices
    }

}
