package com.plocki.alert

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import androidx.lifecycle.ProcessLifecycleOwner
import com.plocki.alert.utils.ApplicationObserver
import com.plocki.alert.utils.MyApolloClient

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        ProcessLifecycleOwner
            .get()
            .lifecycle
            .addObserver(ApplicationObserver())
        val cm = applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork: NetworkInfo? = cm.activeNetworkInfo
        val isConnected: Boolean = activeNetwork?.isConnectedOrConnecting == true
        val myApolloClient = MyApolloClient()
//        myApolloClient.fetchEvents()
        myApolloClient.createEvent()
    }

    override fun onTerminate() {
        super.onTerminate()
    }

}