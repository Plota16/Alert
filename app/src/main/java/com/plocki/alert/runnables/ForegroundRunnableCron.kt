package com.plocki.alert.runnables

import android.util.Log
import com.apollographql.apollo.ApolloCall
import com.apollographql.apollo.api.Response
import com.apollographql.apollo.exception.ApolloException
import com.plocki.alert.API.modules.EventsApi
import com.plocki.alert.AllEventsQuery
import com.plocki.alert.models.Event
import com.plocki.alert.models.Global
import java.lang.Integer.max

class ForegroundRunnableCron : Runnable {
    var isAppClose: Boolean = false

    var seconds: Long = 2000

    override fun run() {
        var i = 0
        while (!isAppClose) {
//                    Handler threadHandler = new Handler(Looper.getMainLooper());
//                    threadHandler.post(new Runnable() {
            println("Pobieram dane z serwera: $i" )
            if(!Global.getInstance()!!.errorActivityOpen){
                EventsApi.fetchEvents(object : ApolloCall.Callback<AllEventsQuery.Data>() {
                    override fun onFailure(e: ApolloException) {
                        Log.e("Źle", e.cause.toString())
                    }

                    override fun onResponse(response: Response<AllEventsQuery.Data>) {
                        val events = response.data()!!.events()
                        Log.d(
                            "AA",
                            "RESPONSE" + response.data()!!.events()
                        )
                        val eventContainer = ArrayList<Event>()
                        val instance = Global.getInstance()
                        for (event in events) {
                            val currentEvent = Event.fromResponse(
                                event.uuid().toString(),
                                event.coords(),
                                event.title(),
                                event.image(),
                                event.description(),
                                1,
                                1
                            )
                            eventContainer.add(currentEvent)
                        }

                        if (Global.getInstance()!!.list.size != eventContainer.size) {
                            Global.getInstance()!!.changed = true
                        }
                        else{
                            for(i in 0 until max(Global.getInstance()!!.list.size,eventContainer.size)){
                                val event1 = Global.getInstance()!!.list[i].UUID
                                val event2 = eventContainer[i].UUID
                                if(event1 != event2){
                                    Global.getInstance()!!.changed = true
                                }
                            }

                        }



                        Global.getInstance()!!.list = eventContainer

                    }
                })
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