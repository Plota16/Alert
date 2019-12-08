package com.plocki.alert.runnables

import com.plocki.alert.API.modules.FetchEventsHandler

class ForegroundRunnableCron : Runnable {
    var isAppClose: Boolean = false

    var seconds: Long = 2000

    override fun run() {
        var i = 0
        while (!isAppClose) {
            try {
                Thread.sleep(seconds)
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
            i++
            println("Pobieram dane z serwera: $i" )

            FetchEventsHandler.fetchEvents()
        }
    }
}