package com.plocki.alert

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.util.Log
import androidx.lifecycle.ProcessLifecycleOwner
import com.apollographql.apollo.ApolloCall
import com.apollographql.apollo.api.Response
import com.apollographql.apollo.exception.ApolloException
import com.plocki.alert.API.modules.EventsApi
import com.plocki.alert.models.Event
import com.plocki.alert.models.Global
import com.plocki.alert.utils.ApplicationObserver
import com.plocki.alert.utils.Store
import java.lang.Exception


class MyApplication : Application() {

    companion object{
        var context : Context? = null

        fun getAppContext(): Context {
            return MyApplication.context!!
        }
    }
    override fun onCreate() {
        super.onCreate()

        ProcessLifecycleOwner
            .get()
            .lifecycle
            .addObserver(ApplicationObserver())
        MyApplication.context = getApplicationContext()


    }

    override fun onTerminate() {
        super.onTerminate()
    }

}