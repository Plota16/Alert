package com.plocki.alert

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.util.Log
import androidx.lifecycle.ProcessLifecycleOwner
import com.apollographql.apollo.ApolloCall
import com.apollographql.apollo.api.Response
import com.apollographql.apollo.exception.ApolloException
import com.plocki.alert.API.modules.EventsApi
import com.plocki.alert.models.Event
import com.plocki.alert.models.Global
import com.plocki.alert.utils.ApplicationObserver

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        ProcessLifecycleOwner
            .get()
            .lifecycle
            .addObserver(ApplicationObserver())
        val cm = applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork: NetworkInfo? = cm.activeNetworkInfo
        val isConnected: Boolean = activeNetwork?.isConnectedOrConnecting == true

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
                    if (!instance!!.list.contains(currentEvent)) {
                        Global.getInstance()!!.list.add(currentEvent)
                    }
                }
            }
        });
    }

    override fun onTerminate() {
        super.onTerminate()
    }

}