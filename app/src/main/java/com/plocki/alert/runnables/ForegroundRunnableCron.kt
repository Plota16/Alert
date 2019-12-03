package com.plocki.alert.runnables

import android.util.Log
import com.apollographql.apollo.ApolloCall
import com.apollographql.apollo.api.Response
import com.apollographql.apollo.exception.ApolloException
import com.plocki.alert.API.modules.EventsApi
import com.plocki.alert.AllEventsQuery
import com.plocki.alert.models.Event
import com.plocki.alert.models.Global

class ForegroundRunnableCron : Runnable {
    var isAppClose: Boolean = false

    var seconds: Long = 2000

    override fun run() {
        var i = 0
        while (!isAppClose) {
//                    Handler threadHandler = new Handler(Looper.getMainLooper());
//                    threadHandler.post(new Runnable() {
            println("Pobieram dane z serwera: $i" )
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
                    Global.getInstance()!!.list = eventContainer
                    val addContainer = eventContainer.minus(Global.getInstance()!!.list)
                    Global.getInstance()!!.toAdd = addContainer as ArrayList<Event>
                    val removeContainer = Global.getInstance()!!.list.minus(eventContainer)
                    Global.getInstance()!!.toRemove = removeContainer as ArrayList<Event>
                }
            })
            try {
                Thread.sleep(seconds)
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
            i++
        }
    }
}