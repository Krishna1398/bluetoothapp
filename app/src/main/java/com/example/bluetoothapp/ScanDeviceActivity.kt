package com.example.bluetoothapp

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.io.FileInputStream
import java.io.OutputStream
import java.util.UUID

class ScanDeviceActivity : AppCompatActivity() {
    private val bluetoothHelper = BluetoothHelper(this)
    lateinit var lvPairedDevices: ListView
    private lateinit var imageUriString: String

    private val REQUEST_BLUETOOTH_PERMISSION = 123
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scan_device)

        imageUriString = intent.getStringExtra("imageUri").toString()


        val btnToggleBluetooth: Button = findViewById(R.id.btnToggleBluetooth)
        val btnShowPairevices: Button = findViewById(R.id.btnShowPairevices)
        lvPairedDevices = findViewById(R.id.lvPairedDevices)


        if (bluetoothHelper.isBluetoothSupported()) {

            btnShowPairevices.setOnClickListener {
                if (!bluetoothHelper.isBluetoothEnabled()) {
                    val enableBluetoothIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED) {
                        // Permission is granted, you can proceed with the operation
                    } else {
                        // Permission is not granted, request it from the user
                        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.BLUETOOTH_CONNECT), REQUEST_BLUETOOTH_PERMISSION)
                    }

                    startActivityForResult(enableBluetoothIntent, REQUEST_ENABLE_BLUETOOTH)
                } else {
                    showPairedDevices()
                }
            }
            btnToggleBluetooth.setOnClickListener {
                toggleBluetooth()
            }
        } else {
            Toast.makeText(this, "Bluetooth is not supported on this device.", Toast.LENGTH_SHORT).show()
        }

    }

    private fun toggleBluetooth() {
        if (bluetoothHelper.isBluetoothEnabled()) {
            bluetoothHelper.disableBluetooth()
        } else {
            bluetoothHelper.enableBluetooth()
        }
    }

    private fun showPairedDevices() {
        val pairedDevices: Set<BluetoothDevice>? = bluetoothHelper.getPairedDevices()
        val devicesList: ArrayList<String>? = ArrayList()

        pairedDevices?.forEach { device ->
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.BLUETOOTH_CONNECT
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return
            }
            devicesList?.add(device.name)

            lvPairedDevices.setOnItemClickListener { parent, view, position, id ->
                val selectedDevice = pairedDevices.elementAtOrNull(position)

                if (selectedDevice != null) {
                    // Call a function to send the image to the selected device
                    sendImageViaBluetooth(selectedDevice)
                }


            }

        }

        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, devicesList!!)
        lvPairedDevices.adapter = adapter
    }

    private fun sendImageViaBluetooth(device: BluetoothDevice) {
        try {
            val randomUUID = UUID.randomUUID()
            val uuidString = randomUUID.toString()

            val socket = device.createRfcommSocketToServiceRecord(UUID.fromString(uuidString))
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.BLUETOOTH_CONNECT
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return
            }
            socket.connect()

            val outputStream: OutputStream = socket.outputStream
            val fis = FileInputStream(imageUriString) // Replace with the path to your image file
            val buffer = ByteArray(1024)
            var bytesRead: Int
            while (true) {
                val bytesRead = fis.read(buffer)
                if (bytesRead == -1) break
                outputStream.write(buffer, 0, bytesRead)
            }

            outputStream.close()
            socket.close()

            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.BLUETOOTH_CONNECT
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return
            }
            Toast.makeText(this, "Image sent via Bluetooth to ${device.name}", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Toast.makeText(this, "Error sending image via Bluetooth", Toast.LENGTH_SHORT).show()
            e.printStackTrace()
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_ENABLE_BLUETOOTH) {
            if (resultCode == RESULT_OK) {
                showPairedDevices()
                Toast.makeText(this, "Bluetooth enabling was canceled.", Toast.LENGTH_SHORT).show()

            } else {
                Toast.makeText(this, "Bluetooth enabling was canceled.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    companion object {
        private const val REQUEST_ENABLE_BLUETOOTH = 1
    }
}