package com.plocki.alert.runnables

import android.content.Intent
import com.plocki.alert.MyApplication
import com.plocki.alert.activities.ConnectionError
import com.plocki.alert.models.EventMethods.Companion.isGpsOn
import com.plocki.alert.models.EventMethods.Companion.isNetworkOn
import com.plocki.alert.models.Global


class ConnectionObserver : Runnable {

    private var gpsEnabled = false
    private var networkEnabled = false

    override fun run() {
        Thread.sleep(1000)
        while (!Global.getInstance()!!.isAppClosed){
            if (!Global.getInstance()!!.isErrorActivityOpen){
                try{
                    gpsEnabled = isGpsOn()
                    networkEnabled = isNetworkOn(MyApplication.getAppContext())
                }catch (ex: Exception ){}

                if(!gpsEnabled || !networkEnabled ){
                    Global.getInstance()!!.isErrorActivityOpen = true
                    val context = MyApplication.getAppContext()
                    val intent = Intent(context, ConnectionError::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    intent.putExtra("SHOW_WELCOME", true)
                    context.startActivity(intent)
                }
            }
            Thread.sleep(2000)
        }

    }



}