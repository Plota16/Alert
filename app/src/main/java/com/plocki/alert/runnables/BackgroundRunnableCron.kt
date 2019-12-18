package com.plocki.alert.runnables

import com.plocki.alert.API.modules.FetchEventsHandler
import com.plocki.alert.models.Global

class BackgroundRunnableCron : Runnable {
    var seconds: Long = 2000

    override fun run() {
        var i = 0
        while (Global.getInstance()!!.isAppClosed) {

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