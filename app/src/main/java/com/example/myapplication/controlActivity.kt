package com.example.myapplication

import android.Manifest
import android.app.ProgressDialog
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.AsyncTask
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import kotlinx.android.synthetic.main.control_layout.*
import java.io.IOException
import java.util.*


class ControlActivity: AppCompatActivity() {

    var count:Int = 0

    companion object {
        var m_myUUID: UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")
        var m_bluetoothSocket: BluetoothSocket? = null
        lateinit var m_progress: ProgressDialog
        lateinit var m_bluetoothAdapter: BluetoothAdapter
        var m_isConnected: Boolean = false
        lateinit var m_address: String
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.control_layout)

        m_address = intent.getStringExtra(MainActivity.EXTRA_ADDRESS).toString()

        val controlLedOn = findViewById<Button>(R.id.control_led_on)
        val controlLedOff = findViewById<Button>(R.id.control_led_off)
        val controlLedDisconnect = findViewById<Button>(R.id.control_led_disconnect)
        var btnLineChart : Button = findViewById(R.id.btnLineChart)
        btnLineChart.setOnClickListener{onClick(btnLineChart)}

        ConnectToDevice(this).execute()
        controlLedOn.setOnClickListener() { sendCommand("a") }
        controlLedOff.setOnClickListener { content(receivedMessageTextView) }
        controlLedDisconnect.setOnClickListener { disconnect() }
    }

    fun onClick(v: View) {
        var intent : Intent = Intent(this,LineChartActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun content(textView: TextView) {
        //val count1 = count++
        //val string1 = "refreshed: $count1"
        if (m_bluetoothSocket != null) {
            var buffer = ByteArray(256)
            val bytes: Int
            //var x:Int = 2
            //while (x > 0) {

            //m_bluetoothSocket!!.outputStream.write(input.toByteArray()
            bytes = m_bluetoothSocket!!.inputStream.read(buffer)
            val readMessage = String(buffer, 0, bytes)
            var stringNumbers: List<String?> = readMessage.split("\n")
            //textView.text  = readMessage
            textView.text = stringNumbers[0]

        }

        refresh (0,textView);
    }

    private fun refresh(milliseconds:Long, textView:TextView) {
        var handler = Handler()
        var runnable = Runnable() {
            content(textView)
        }
        handler.postDelayed(runnable, milliseconds)
    }

    private fun sendCommand(input: String) {
        if (m_bluetoothSocket != null) {
            var buffer = ByteArray(256)
            var bytes: Int
            try{
                //var x:Int = 2
                //while (x > 0) {

                    //m_bluetoothSocket!!.outputStream.write(input.toByteArray()
                    bytes = m_bluetoothSocket!!.inputStream.read(buffer)
                    val readMessage = String(buffer, 0, bytes)
                    receivedMessageTextView.text = readMessage

                   // x--
                //}



            } catch(e: IOException) {
                e.printStackTrace()
            }
        }
    }

    private fun disconnect() {
        if (m_bluetoothSocket != null) {
            try {
                m_bluetoothSocket!!.close()
                m_bluetoothSocket = null
                m_isConnected = false
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        finish()
    }

    private class ConnectToDevice(c: Context) : AsyncTask<Void, Void, String>() {
        private var connectSuccess: Boolean = true
        private val context: Context

        init {
            this.context = c
        }

        override fun onPreExecute() {
            super.onPreExecute()
            m_progress = ProgressDialog.show(context, "Connecting...", "please wait")
        }

        override fun doInBackground(vararg p0: Void?): String? {

            try {

                if (m_bluetoothSocket == null || !m_isConnected) {
                    m_bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
                    val device: BluetoothDevice = m_bluetoothAdapter.getRemoteDevice(m_address)
                    if (ActivityCompat.checkSelfPermission(
                            context,
                            Manifest.permission.BLUETOOTH_CONNECT
                        ) != PackageManager.PERMISSION_GRANTED
                    ) {
                        return null
                    }
                    m_bluetoothSocket = device.createInsecureRfcommSocketToServiceRecord(m_myUUID)
                    BluetoothAdapter.getDefaultAdapter().cancelDiscovery()
                    m_bluetoothSocket!!.connect()
                }
            } catch (e: IOException) {
                connectSuccess = false
                e.printStackTrace()
            }
            return null
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            if (!connectSuccess) {
                Log.i("data", "couldn't connect")
            } else {
                m_isConnected = true
            }
            m_progress.dismiss()
        }

    }
}