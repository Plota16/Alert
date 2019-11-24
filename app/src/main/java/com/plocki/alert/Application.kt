package com.plocki.alert

import android.app.Application
import androidx.lifecycle.ProcessLifecycleOwner
import com.plocki.alert.utils.ApplicationObserver

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        ProcessLifecycleOwner
            .get()
            .lifecycle
            .addObserver(ApplicationObserver())
    }

    override fun onTerminate() {
        super.onTerminate()
    }

}