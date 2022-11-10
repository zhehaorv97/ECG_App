package com.example.myapplication

import android.content.pm.ActivityInfo
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet


class LineChartActivity : AppCompatActivity()  {
    private val data = FloatArray(64)
    private var size:Int = 0
   // private var dataAvailable = false
    private var index:Int = 0
    private var values: ArrayList<Entry> = ArrayList()
    private val sampleTime = 1.0f / 10f
    private lateinit var lineChart: LineChart
    private val updatePeriod: Long = 60
    private var dataAvailable = false
    var buffer = ByteArray(256)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        setContentView(R.layout.activity_line_chart)
        //val back = findViewById<Button>(R.id.back)

        lineChart = findViewById(R.id.lineChart)
        lineChart.setDrawGridBackground(false)
        lineChart.getDescription().setEnabled(false)
        lineChart.setTouchEnabled(false)
        lineChart.setScaleY(1.0f)
        lineChart.setPinchZoom(false)
        lineChart.getAxisLeft().setDrawGridLines(false)
        lineChart.getAxisRight().setEnabled(false)
        lineChart.getXAxis().setDrawGridLines(false)
        lineChart.getXAxis().setDrawAxisLine(false)

        if (ControlActivity.m_bluetoothSocket != null) {
        var buffer = ByteArray(256)
        val bytes: Int

        bytes = ControlActivity.m_bluetoothSocket!!.inputStream.read(buffer)

        var readMessage = String(buffer, 0, bytes)
        var stringNumbers: List<String?> = readMessage.split("\n")

        messageHandler(stringNumbers)}

        for (i in 0..512) {
            values.add(Entry(i * sampleTime, 160f))
        }



        updateChart()

        /*var handler = Handler()
        handler.postDelayed(object : Runnable {
            override fun run() {
                handler.postDelayed(this, updatePeriod)
                updateRoutine(values)
            }
        }, updatePeriod)*/

        content()

        //back.setOnClickListener { finish() }


    }

    /*private val mGattUpdateReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent) {
            if (ControlActivity.m_bluetoothSocket != null) {
                var buffer = ByteArray(256)
                val bytes: Int
                //var x:Int = 2
                //while (x > 0) {

                //m_bluetoothSocket!!.outputStream.write(input.toByteArray()
                bytes = ControlActivity.m_bluetoothSocket!!.inputStream.read(buffer)
                var readMessage = String(buffer, 0, bytes)
                var stringNumbers: List<String?> = readMessage.split("\n")
                messageHandler(stringNumbers)
            }}}*/

    private fun updateChart() {
        lineChart.resetTracking()
        setData(values)
        // redraw
        lineChart.invalidate()
    }


    private fun content() {
        //val count1 = count++
        //val string1 = "refreshed: $count1"
        if (ControlActivity.m_bluetoothSocket != null) {
            val bytes: Int
            //var x:Int = 2
            //while (x > 0) {

            //m_bluetoothSocket!!.outputStream.write(input.toByteArray()
            bytes = ControlActivity.m_bluetoothSocket!!.inputStream.read(buffer)
            var readMessage = String(buffer, 0, bytes)
            var stringNumbers: List<String?> = readMessage.split("\n")
            messageHandler(stringNumbers)
            updateRoutine(values)
            setData(values)
            updateChart()
            if (bytes>320){
            buffer = ByteArray(256)}

        }

        refresh (   25)

    }

    private fun refresh(milliseconds:Long) {
        var handler = Handler()
        var runnable = Runnable() {
            content()
        }
        handler.postDelayed(runnable, milliseconds)
    }

    private fun messageHandler(stringNumbers: List<String?> ){
        size=0
        var x: Int = 0
        for (i in stringNumbers.indices) {
            size++
            var value: Float
            try {
                value = stringNumbers[i]!!.toFloat()
            } catch (e: Exception) {
                value = 0f
                e.printStackTrace()
                println("Parsing error.")
            }
            if (value > 4095 || value < 0) println("Weird number[$i]: $value") else {
                if (i < 40) {
                    try {
                        data[i-x]= stringNumbers[i]!!.toFloat()
                    } catch (e: Exception) {
                        x+=1
                        if(size>1)size--
                        e.printStackTrace()
                        //data[i] = 500f //2048.0f
                    }
                } else println("Extra number[" + i + "]: " + stringNumbers[i]!!.toFloat())
            }
        }
        dataAvailable = true
    }

    private fun updateRoutine(dataSet:ArrayList<Entry>){
        index %= 510
        //val dataSet = ArrayList<Entry>()
        if (dataAvailable) {
            //for (i in 0..1) {
                //dataSet.removeAt(index)
                dataSet.set(index,Entry(index * sampleTime, data[index % size]))
                //dataSet[index] = Entry(index * sampleTime, data[index % 64])
                index++
            //}
            if (index % 64 == 0) {
                dataAvailable = false
                println("Waiting for new data.")
            }
            updateChart()
        } else println("Runnable is doing nothing...")
    }




    private fun setData(values:ArrayList<Entry>) {

        // create a dataset and give it a type
        val set1 = LineDataSet(values, "ECG")
        set1.color = Color.RED
        set1.lineWidth = 1.0f
        set1.setDrawValues(false)
        set1.setDrawCircles(false)
        set1.mode = LineDataSet.Mode.LINEAR
        set1.setDrawFilled(false)

        // create a data object with the data sets
        val data = LineData(set1)

        // set data
        lineChart.setData(data)
        lineChart.setVisibleYRange(-128F, 127F, null)

        val l: Legend = lineChart.getLegend()
        l.isEnabled = false
    }
}