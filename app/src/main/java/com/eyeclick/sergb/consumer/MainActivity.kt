package com.eyeclick.sergb.consumer

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.os.*
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast


class MainActivity : AppCompatActivity() {
    val REQUEST_BOOK = 101
    val READ_BULK = 201
    val READ_END = 202

    var textview: TextView? = null;
    var button: Button? = null;

    var isBound = false
    var mMessenger: Messenger? = null
    private val mActivityMessenger = Messenger(
            ActivityHandler())

    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: HistagramAdapter
    private lateinit var viewManager: RecyclerView.LayoutManager

    var histogram: MutableMap<Char, Float>? = HashMap()
    var tempHistogram: MutableMap<Char, Int>? = HashMap()
    var totalSum: Int = 0
    val bulkProcess: BulkProcess = BulkProcess()


    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            isBound = true

            // Create the Messenger object
            mMessenger = Messenger(service)
            textview?.text = "Service is ready"
            button?.isEnabled = true
        }

        override fun onServiceDisconnected(name: ComponentName) {
            // unbind or process might have crashes
            mMessenger = null
            isBound = false
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initViews()

    }

    override fun onResume() {
        super.onResume()

        if (checkIfProducerInstalled()) {
            bindRemoteService()
        } else {
            textview?.text = "Please Install Producer App first";
        }
    }

    override fun onStop() {
        super.onStop()
        if (isBound) {
            unbindService(serviceConnection)
            button?.isEnabled = false
            isBound = false
            mMessenger = null
        }
    }

    fun checkIfProducerInstalled(): Boolean {
        val pm = getPackageManager()
        try {
            packageManager.getPackageInfo("com.eyeclick.sergb.producer", 0)
            return true
        } catch (e: PackageManager.NameNotFoundException) {
            return false
        }
    }

    fun initViews() {
        textview = findViewById<TextView>(R.id.textView)
        button = findViewById<Button>(R.id.button)
        viewManager = LinearLayoutManager(this)
        viewAdapter = HistagramAdapter(histogram)

        recyclerView = findViewById<RecyclerView>(R.id.recyclerView).apply {
            // use this setting to improve performance if you know that changes
            // in content do not change the layout size of the RecyclerView
            setHasFixedSize(true)

            // use a linear layout manager
            layoutManager = viewManager

            // specify an viewAdapter (see also next example)
            adapter = viewAdapter

        }
    }

    fun bindRemoteService() {
        if (!isBound) {
            val intent = Intent("com.eyeclick.sergb.producer.service")
            intent.`package` = "com.eyeclick.sergb.producer"
            bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
        }
    }

    // click response to button. sending message to service app
    fun btnClick(view: View) {
        if (!isBound) {
            bindRemoteService()
            Toast.makeText(this, "Trying to Bind Service", Toast.LENGTH_LONG).show()
            return
        }
        button?.isEnabled = false
        textview?.text = "Receiving Data..."
        // Send the Message to the Service (in another process)
        val msg = Message.obtain(null, REQUEST_BOOK, 0, 0)
        msg.replyTo = mActivityMessenger
        try {
            mMessenger!!.send(msg)
        } catch (e: RemoteException) {
            e.printStackTrace()
        }
    }

    // message handler to receive messages from service.
    inner class ActivityHandler() : Handler() {
        override fun handleMessage(msg: Message) {
            when (msg.what) {
                READ_BULK -> {
                    var chunk: String = msg.data.getString("chunk")
                    chunk = bulkProcess.stripWhitespace(chunk)
                    tempHistogram = bulkProcess.processBulk(chunk, tempHistogram!!)

                    totalSum += chunk.length
                }
                READ_END -> {
                    histogram = bulkProcess.calculateHistogram(tempHistogram!!, totalSum)

                    textview?.text = "Data Received"
                    button?.isEnabled = true
                    viewAdapter.setItems(histogram)
                    viewAdapter.notifyDataSetChanged()
                }
            }
        }
    }
}
