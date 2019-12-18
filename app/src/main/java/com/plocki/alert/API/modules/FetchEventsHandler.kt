package com.plocki.alert.API.modules

import android.app.Activity
import android.content.Intent
import android.util.Log
import android.widget.Toast
import com.apollographql.apollo.ApolloCall
import com.apollographql.apollo.api.Response
import com.apollographql.apollo.exception.ApolloException
import com.plocki.alert.API.ApolloInstance
import com.plocki.alert.AllEventsQuery
import com.plocki.alert.activities.MainActivity
import com.plocki.alert.models.Category
import com.plocki.alert.models.Event
import com.plocki.alert.models.Global
import java.util.*
import kotlin.collections.ArrayList

object FetchEventsHandler {

    fun fetchEvents(activity: Activity? = null, isLoginPanel: Boolean = false) {
        if(!Global.getInstance()!!.isErrorActivityOpen && Global.getInstance()!!.isUserSigned){

            ApolloInstance.buildApolloClient()
            EventsApi.fetchEvents(object : ApolloCall.Callback<AllEventsQuery.Data>() {
                override fun onFailure(e: ApolloException) {
                    activity?.runOnUiThread { Toast.makeText(activity, "Nie udało się pobrać danych z serwera", Toast.LENGTH_SHORT).show() }
                    Log.e("ERROR FETCH", e.cause.toString())
                }

                override fun onResponse(response: Response<AllEventsQuery.Data>) {

                    if (response.data() != null) {
                        val events = response.data()!!.events()
                        val eventContainer = ArrayList<Event>()
                        for (event in events) {
                            val currentEvent = Event.fromResponse(
                                event.uuid().toString(),
                                event.coords(),
                                event.title(),
                                event.image(),
                                event.description(),
                                Category(
                                    event.category()!!.uuid().toString(),
                                    event.category()!!.title(),
                                    event.category()!!.color()),
                                1
                            )
                            eventContainer.add(currentEvent)
                        }

                        if (Global.getInstance()!!.eventList.size != eventContainer.size) {
                            if(Global.getInstance()!!.isDataLoadedFirstTime){
                                Global.getInstance()!!.isDataLoadedFirstTime = false
                            }
                            else{
                                Global.getInstance()!!.isDataChanged = true
                            }
                        } else {
                            for (i in 0 until Integer.max(
                                Global.getInstance()!!.eventList.size,
                                eventContainer.size
                            )) {
                                val event1 = Global.getInstance()!!.eventList[i].UUID
                                val event2 = eventContainer[i].UUID
                                if (event1 != event2) {
                                    if(Global.getInstance()!!.isDataLoadedFirstTime){
                                        Global.getInstance()!!.isDataLoadedFirstTime = false
                                    }
                                    else{
                                        Global.getInstance()!!.isDataChanged = true
                                    }
                                }
                            }

                        }

                        Global.getInstance()!!.eventList = eventContainer


                    } else {
                        activity?.runOnUiThread { Toast.makeText(activity, "Nie udało się pobrać danych z serwera", Toast.LENGTH_SHORT).show()}
                    }

                    if (isLoginPanel && activity != null) {
                        val intent = Intent(activity, MainActivity::class.java)
                        intent.putExtra("SHOW_WELCOME", true)
                        activity.startActivity(intent)
                        activity.finish()
                    }

                }

            })
        }
    }
}