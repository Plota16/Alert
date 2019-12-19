package com.plocki.alert

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import androidx.lifecycle.ProcessLifecycleOwner
import com.plocki.alert.utils.ApplicationObserver

class MyApplication : Application() {

    companion object{
        @SuppressLint("StaticFieldLeak")
        var context : Context? = null

        fun getAppContext(): Context {
            return context!!
        }
    }
    override fun onCreate() {
        super.onCreate()

        ProcessLifecycleOwner
            .get()
            .lifecycle
            .addObserver(ApplicationObserver())
        context = applicationContext


    }

}