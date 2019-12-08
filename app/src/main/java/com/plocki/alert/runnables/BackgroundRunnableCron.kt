package com.plocki.alert.runnables

import com.plocki.alert.API.modules.FetchEventsHandler

class BackgroundRunnableCron : Runnable {
    var isAppClose: Boolean = false

    var seconds: Long = 2000

    override fun run() {
        var i = 0
        while (isAppClose) {
//                    Handler threadHandler = new Handler(Looper.getMainLooper());
//                    threadHandler.post(new Runnable() {
            println("Pobieram back dane z serwera: $i" )
            FetchEventsHandler.fetchEvents()
            try {
                Thread.sleep(seconds)
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
            i++
        }
    }

}