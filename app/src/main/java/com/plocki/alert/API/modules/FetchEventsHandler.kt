package com.plocki.alert.API.modules

import android.app.Activity
import android.content.Intent
import android.util.Log
import android.widget.Toast
import com.apollographql.apollo.ApolloCall
import com.apollographql.apollo.api.Response
import com.apollographql.apollo.exception.ApolloException
import com.plocki.alert.AllEventsQuery
import com.plocki.alert.activities.MainActivity
import com.plocki.alert.models.Event
import com.plocki.alert.models.Global

object FetchEventsHandler {

    fun fetchEvents(activity: Activity? = null, isLoginPanel: Boolean = false) {
        if(!Global.getInstance()!!.errorActivityOpen && Global.getInstance()!!.logged){
            EventsApi.fetchEvents(object : ApolloCall.Callback<AllEventsQuery.Data>() {
                override fun onFailure(e: ApolloException) {
                    if (activity != null) {
                        Toast.makeText(activity, "Nie udało się pobrać danych z serwera", Toast.LENGTH_SHORT).show()
                    }
                    Log.e("ERROR FETCH", e.cause.toString())
                }

                override fun onResponse(response: Response<AllEventsQuery.Data>) {
                    if (activity != null) {
                        activity.runOnUiThread {
                            Toast.makeText(
                                activity,
                                "Pobrano dane z serwera",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                    try {
                        val events = response.data()!!.events()
                        Log.d("EVENTS", response.data()!!.events().toString())
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
                        } else {
                            for (i in 0 until Integer.max(
                                Global.getInstance()!!.list.size,
                                eventContainer.size
                            )) {
                                val event1 = Global.getInstance()!!.list[i].UUID
                                val event2 = eventContainer[i].UUID
                                if (event1 != event2) {
                                    Global.getInstance()!!.changed = true
                                }
                            }

                        }


                        Global.getInstance()!!.list = eventContainer
                        if (isLoginPanel && activity != null) {
                            val intent = Intent(activity, MainActivity::class.java)
                            intent.putExtra("SHOW_WELCOME", true)
                            activity.startActivity(intent)
                            activity.finish()
                        }

                    } catch (e: Exception) {
                        Toast.makeText(activity, "Nie udało się pobrać danych z serwera", Toast.LENGTH_SHORT).show()
                    }
                }

            })
        }
    }
}