package com.plocki.alert.utils

import android.graphics.Point
import android.net.Uri
import android.os.Environment
import android.util.Log
import com.apollographql.apollo.ApolloCall
import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.api.FileUpload
import com.apollographql.apollo.api.Input
import com.apollographql.apollo.api.Query
import com.apollographql.apollo.api.Response
import com.apollographql.apollo.exception.ApolloException
import com.google.android.gms.maps.model.LatLng
import com.plocki.alert.AllEventsQuery
import com.plocki.alert.CreateEventMutation
import com.plocki.alert.models.Event
import com.plocki.alert.models.Global
import com.plocki.alert.type.CreateEventDto
import com.plocki.alert.type.PointInput
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.internal.wait
import okhttp3.logging.HttpLoggingInterceptor
import java.io.ByteArrayInputStream
import java.io.File
import java.lang.reflect.Array
import java.util.*
import kotlin.collections.ArrayList

class MyApolloClient {

    var apolloClient: ApolloClient? = null
    var token: String = ""
    var BASE_URL = "http://192.168.1.229:3000/graphql"

    init {
        setToken()
        val httpLoggingInterceptor = HttpLoggingInterceptor()
        httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor { chain ->
                val original = chain.request()
                val builder = original.newBuilder().method(original.method, original.body)
                builder.header(
                    "Authorization",
                    "Bearer ${this.token}"
                )
                chain.proceed(builder.build())
            }
            .build()
        apolloClient = ApolloClient.builder()
            .serverUrl(BASE_URL)
            .okHttpClient(okHttpClient)
            .build()
    }

    fun setToken() {
        this.token =
            "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1dWlkIjoiYzk0ZjRjNDAtZjFkYy00OTVhLTkxZmItY2E4ZTRkZDFhNjllIiwidG9rZW5JZCI6NTI0ODc0MCwiaWF0IjoxNTc1MzIwMDYyLCJleHAiOjE1NzY2MTYwNjJ9.gTx6Q16Z7exlNOASOIrm5AfuINPXk3uxFzJp_SWYsEw"
    }

    fun createEvent(event: Event): Boolean {
        println(event)
        var createEventResult = false
        val image = File(event.image)
        val createEventDto = CreateEventDto.builder()
            .title(event.title)
            .description(event.desctription)
            .imageData(FileUpload("image/jpg", image))
            .coords(
                (PointInput.builder()
                    .x(event.coords.latitude)
                    .y(event.coords.longitude).build())
            ).build()

        apolloClient!!.mutate(
            CreateEventMutation.builder().eventData(createEventDto)
                .build()
        )
            .enqueue(object : ApolloCall.Callback<CreateEventMutation.Data>() {
                override fun onFailure(e: ApolloException) {
                    Log.e("ERROR", e.cause.toString())
                }

                override fun onResponse(response: Response<CreateEventMutation.Data>) {
                    createEventResult = true
                    fetchEvents()
                    Log.d("SUCCESS", response.data().toString())
                }
            })
        return createEventResult
    }

    fun fetchEvents(){
        apolloClient!!.query(
            AllEventsQuery.builder().build()
        ).enqueue(object : ApolloCall.Callback<AllEventsQuery.Data>() {
            override fun onFailure(e: ApolloException) {

                Log.e("Źle", e.cause.toString())
            }

            override fun onResponse(response: Response<AllEventsQuery.Data>) {
                val events = response.data()!!.events()
                Log.d(
                    "AA",
                    "RESPONSE" + response.data()!!.events()
                )
                var result = ArrayList<Event>()
                val global = Global.getInstance()
                global!!.toAdd.clear()
                global.toRemove.clear()

                var i = 0
                for (event in events) {

                    val currentEvent = Event(
                        i,
                        UUID.fromString(event.uuid().toString()),
                        LatLng(event.coords().x(), event.coords().y()),
                        event.image().orEmpty().plus(""),
                        event.title(),
                        "opis",
                        1,
                        1
                    )
                    i++
                       result.add(currentEvent)

                }

                val addContainer = result.minus(global!!.list)
                global.toAdd = addContainer as ArrayList<Event>

                val removeContainer = global.list.minus(result)
                global.toRemove = removeContainer as ArrayList<Event>


                global.list = result

            }


        })

    }
}