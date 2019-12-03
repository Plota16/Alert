package com.plocki.alert.utils

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import com.plocki.alert.runnables.BackgroundRunnableCron
import com.plocki.alert.runnables.ForegroundRunnableCron

class ApplicationObserver : LifecycleObserver {

    private val foregroundRunnable: ForegroundRunnableCron =
        ForegroundRunnableCron()
    private val backgroundRunnable: BackgroundRunnableCron =
        BackgroundRunnableCron()

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun onForeground() {
        backgroundRunnable.isAppClose = false
        foregroundRunnable.isAppClose = false
        foregroundRunnable.seconds = 1000
        val thread = Thread(foregroundRunnable)
        thread.start()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun onBackground() {
        foregroundRunnable.isAppClose = true
        backgroundRunnable.isAppClose = true
        backgroundRunnable.seconds = 600000
        val thread = Thread(backgroundRunnable)
        thread.start()
    }

}