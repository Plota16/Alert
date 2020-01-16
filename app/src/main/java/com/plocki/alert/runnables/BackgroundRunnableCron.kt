package com.plocki.alert.runnables

import com.plocki.alert.api.modules.FetchEventsHandler

class BackgroundRunnableCron : Runnable {
    var seconds: Long = 2000
    private var isAlive = true

    override fun run() {
        var i = 0
        while (isAlive) {
            println("Pobieram back dane z serwera: $i" )
            FetchEventsHandler.fetchEvents()
            try {
                Thread.sleep(seconds)
            } catch (e: InterruptedException) {
                println("Tylni wątek aktualizujący zakończony")
                break
            }
            i++
        }
    }

}