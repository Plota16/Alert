package com.plocki.alert.runnables

import com.plocki.alert.utils.MyApolloClient

class ForegroundRunnableCron : Runnable {
    var isAppClose: Boolean = false

    var seconds: Long = 2000

    override fun run() {
        var i = 0
        while (!isAppClose) {
//                    Handler threadHandler = new Handler(Looper.getMainLooper());
//                    threadHandler.post(new Runnable() {
            println("Pobieram dane z serwera: $i" )
            val apolloClient = MyApolloClient()
            apolloClient.fetchEvents()
            try {
                Thread.sleep(seconds)
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
            i++
        }
    }

}