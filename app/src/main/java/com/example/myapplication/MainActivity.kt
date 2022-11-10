package com.example.myapplication

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    var context: Context? = null
    private var mbluetoothAdapter: BluetoothAdapter? = null
    private lateinit var mpairedDevices: Set<BluetoothDevice>
    lateinit var bluetoothManager: BluetoothManager
    lateinit var bluetoothAdapter: BluetoothAdapter
    lateinit var onBluetoothEnableResult: ActivityResultLauncher<Intent>

    companion object {
        val EXTRA_ADDRESS: String = "Device_address"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        bluetoothManager = getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        mbluetoothAdapter = bluetoothManager.adapter

        if(mbluetoothAdapter == null) {
            Toast.makeText(applicationContext,"this device doesn't support bluetooth", Toast.LENGTH_SHORT).show()
            return
        }
        if(!mbluetoothAdapter!!.isEnabled) {
            val enableBluetoothIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)

            onBluetoothEnableResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ result ->
                if (result.resultCode == RESULT_OK) {
                    if (mbluetoothAdapter!!.isEnabled) {
                        Toast.makeText(applicationContext,"Bluetooth has been enabled", Toast.LENGTH_SHORT).show()
                    }
                    else {
                        Toast.makeText(applicationContext,"Bluetooth has been disabled", Toast.LENGTH_SHORT).show()
                    }
                }
                else if (result.resultCode == RESULT_CANCELED) {
                    Toast.makeText(applicationContext,"Bluetooth enabling has been canceled", Toast.LENGTH_SHORT).show()
                }
            }
        }

        select_device_refresh.setOnClickListener{ pairedDeviceList() }
    }

    private fun pairedDeviceList(){
        //context = this
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.BLUETOOTH_CONNECT
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        mpairedDevices = mbluetoothAdapter!!.bondedDevices
        val list : ArrayList<BluetoothDevice> = ArrayList()

        if (mpairedDevices.isNotEmpty()) {
            for (device: BluetoothDevice in mpairedDevices) {
                list.add(device)
                Log.i("device", ""+device)
            }
        } else {
            Toast.makeText(applicationContext,"no paired bluetooth devices found", Toast.LENGTH_SHORT).show()
        }

        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, list)
        select_device_list.adapter = adapter
        select_device_list.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
            val device: BluetoothDevice = list[position]
            val address: String = device.address

            val intent = Intent(this, ControlActivity::class.java)
            intent.putExtra(EXTRA_ADDRESS, address)
            startActivity(intent)
        }
    }


}











