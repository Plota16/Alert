package com.plocki.alert.activities

import android.annotation.SuppressLint
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.plocki.alert.utils.ConnectivityReceiver


/** Author : http://devdeeds.com
 *  Project : Sample Project - Internet status checking
 *  Date : 24 Feb 2018*/


//THIS IS THE BASE ACTIVITY OF ALL ACTIVITIES OF THE APPLICATION.

@SuppressLint("Registered")
open class BaseActivity : AppCompatActivity(), ConnectivityReceiver.ConnectivityReceiverListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        registerReceiver(ConnectivityReceiver(),
            IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION))
    }


    private fun showMessage(isConnected: Boolean) {


    println("JEStESMEMSMEM")
        if (!isConnected) {

            val messageToUser = "You are offline now." //TODO
            Toast.makeText(applicationContext, messageToUser, Toast.LENGTH_SHORT).show() //Assume "rootLayout" as the root layout of every activity.
        } else {
            Toast.makeText(applicationContext, "You are online", Toast.LENGTH_SHORT).show() //Assume "rootLayout" as the root layout of every activity.
        }


    }

    override fun onResume() {
        super.onResume()

        ConnectivityReceiver.connectivityReceiverListener = this
    }

    /**
     * Callback will be called when there is change
     */
    override fun onNetworkConnectionChanged(isConnected: Boolean) {
        showMessage(isConnected)
    }
}