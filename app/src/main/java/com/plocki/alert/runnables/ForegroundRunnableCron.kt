package com.plocki.alert.runnables

import com.plocki.alert.api.modules.FetchEventsHandler

class ForegroundRunnableCron : Runnable {
    var seconds: Long = 6000
    private var isAlive = true

    override fun run() {
        var i = 0
        while (isAlive) {
            i++
            println("Pobieram dane z serwera: $i" )
            FetchEventsHandler.fetchEvents()
            try {
                    Thread.sleep(seconds)
            } catch (e: InterruptedException) {
                println("Wierzchni wątek aktualizujący zakończony")
                break
            }
        }
    }
}