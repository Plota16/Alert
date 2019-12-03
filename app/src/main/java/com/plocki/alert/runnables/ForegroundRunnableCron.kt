package com.plocki.alert.runnables

import com.plocki.alert.models.Global
import com.plocki.alert.utils.MyApolloClient
import kotlinx.coroutines.GlobalScope

class ForegroundRunnableCron : Runnable {
    var isAppClose: Boolean = false

    var seconds: Long = 2000

    val inst = Global.getInstance()
    override fun run() {
        var i = 0
        while (!isAppClose) {
//                    Handler threadHandler = new Handler(Looper.getMainLooper());
//                    threadHandler.post(new Runnable() {
            println("Pobieram dane z serwera: $i" )
            val myApolloClient = MyApolloClient()
            myApolloClient.fetchEvents()
            if(inst!!.toAdd.size != 0|| inst.toRemove.size != 0){

            }

            try {
                Thread.sleep(seconds)
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
            i++
        }
    }

}