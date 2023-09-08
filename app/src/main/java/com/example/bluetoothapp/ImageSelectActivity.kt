package com.example.bluetoothapp

import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class ImageSelectActivity : AppCompatActivity() {
    private val REQUEST_BLUETOOTH_PERMISSION = 1
    private val REQUEST_SELECT_IMAGE = 2
    private val bluetoothAdapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image_select)

        // Check if Bluetooth is supported on the device
        if (bluetoothAdapter == null) {
            // Bluetooth is not supported
            return
        }

        // Request permission to access Bluetooth if needed
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.BLUETOOTH), REQUEST_BLUETOOTH_PERMISSION)
        }

        // Button to select image from gallery
        val selectImageButton: Button = findViewById(R.id.select_image_button)
        selectImageButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent, REQUEST_SELECT_IMAGE)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_BLUETOOTH_PERMISSION -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission granted, continue with Bluetooth operations
                } else {
                    // Permission denied, handle accordingly
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_SELECT_IMAGE && resultCode == RESULT_OK && data != null) {
            val imageUri = data.data
            // Transfer the image via Bluetooth
//            val transferImageIntent = Intent(Intent.ACTION_SEND)
            val transferImageIntent = Intent(this,ScanDeviceActivity::class.java)
            transferImageIntent.type = "image/*"
            transferImageIntent.putExtra(Intent.EXTRA_STREAM, imageUri)
//            transferImageIntent.`package` = "com.android.bluetooth" // Package name for Bluetooth on most Android devices

            startActivity(Intent.createChooser(transferImageIntent, "Share image via Bluetooth"))
        }
    }
}