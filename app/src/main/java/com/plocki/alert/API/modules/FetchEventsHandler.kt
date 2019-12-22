package com.plocki.alert.API.modules

import android.app.Activity
import android.content.Intent
import android.util.Log
import android.widget.Toast
import com.apollographql.apollo.ApolloCall
import com.apollographql.apollo.api.Response
import com.apollographql.apollo.exception.ApolloCanceledException
import com.apollographql.apollo.exception.ApolloException
import com.apollographql.apollo.exception.ApolloHttpException
import com.google.gson.GsonBuilder
import com.plocki.alert.API.ApolloInstance
import com.plocki.alert.AllEventsQuery
import com.plocki.alert.MyApplication
import com.plocki.alert.activities.MainActivity
import com.plocki.alert.models.Category
import com.plocki.alert.models.Event
import com.plocki.alert.models.Global
import com.plocki.alert.models.LikeType
import com.plocki.alert.utils.HttpErrorHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlin.collections.ArrayList

object FetchEventsHandler {

    fun fetchEvents(activity: Activity? = null, isLoginPanel: Boolean = false, finish: Boolean = false, startMain: Boolean = false) {
        if(!Global.getInstance()!!.isErrorActivityOpen && Global.getInstance()!!.isUserSigned){

            ApolloInstance.buildApolloClient()
            EventsApi.fetchEvents(object : ApolloCall.Callback<AllEventsQuery.Data>() {
                override fun onFailure(e: ApolloException) {
                    //TODO sprawdzić czy zakomentować linię niżej
                    activity?.runOnUiThread { Toast.makeText(activity, "Nie udało się pobrać danych z serwera", Toast.LENGTH_SHORT).show() }
                    HttpErrorHandler.handle(500)
                }

                override fun onResponse(response: Response<AllEventsQuery.Data>) {
                    if (response.hasErrors()) {
                        Log.e("ERROR ", response.errors()[0].customAttributes()["statusCode"].toString())
                        val gson = GsonBuilder().create()
                        val errorMap = gson.fromJson(response.errors()[0].message(), Map::class.java)
                        HttpErrorHandler.handle(errorMap["statusCode"].toString().toFloat().toInt())
                        return
                    }

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
                                1,
                                event.totalLikes(),
                                LikeType.getNameByType(event.userLike())!!
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

                    //Options
                    if (isLoginPanel && activity != null) {
                        val intent = Intent(activity, MainActivity::class.java)
                        intent.putExtra("SHOW_WELCOME", true)
                        activity.startActivity(intent)
                        activity.finish()
                    }
                    if(finish &&  activity != null){
                        activity.finish()
                    }
                    if(startMain && activity != null){
                        GlobalScope.launch(Dispatchers.Main){
                            Global.getInstance()!!.areCategoriesLoaded = true

                            val intent = Intent(MyApplication.context, MainActivity::class.java)
                            intent.putExtra("SHOW_WELCOME", true)
                            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                            MyApplication.context!!.startActivity(intent)
                        }
                    }

                }

            })
        }
    }
}