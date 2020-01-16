package com.plocki.alert.utils

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import com.plocki.alert.models.Global
import com.plocki.alert.runnables.BackgroundRunnableCron
import com.plocki.alert.runnables.ConnectionObserver
import com.plocki.alert.runnables.ForegroundRunnableCron

class ApplicationObserver : LifecycleObserver {

    private val foregroundRunnable: ForegroundRunnableCron = ForegroundRunnableCron()
    private val backgroundRunnable: BackgroundRunnableCron = BackgroundRunnableCron()
    private val connectionObserver: ConnectionObserver = ConnectionObserver()

    private var foregroundthread = Thread(foregroundRunnable)
    private var connectionThread = Thread(connectionObserver)
    private var backgroudthread = Thread(backgroundRunnable)

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun onForeground() {
        Global.getInstance()!!.isAppClosed = false
        foregroundRunnable.seconds = 6000

        foregroundthread = Thread(foregroundRunnable)
        connectionThread = Thread(connectionObserver)

        if(backgroudthread.isAlive){
            backgroudthread.interrupt()
        }

        foregroundthread.start()
        connectionThread.start()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun onBackground() {
        Global.getInstance()!!.isAppClosed = true
        backgroundRunnable.seconds = 600000
        backgroudthread = Thread(backgroundRunnable)

        if(foregroundthread.isAlive){
            foregroundthread.interrupt()
        }
        if(connectionThread.isAlive){
            connectionThread.interrupt()
        }

        backgroudthread.start()
    }

}