package com.plocki.alert.utils

import android.graphics.Point
import android.net.Uri
import android.os.Environment
import android.util.Log
import com.apollographql.apollo.ApolloCall
import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.api.FileUpload
import com.apollographql.apollo.api.Input
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
import okhttp3.logging.HttpLoggingInterceptor
import java.io.ByteArrayInputStream
import java.io.File
import java.util.*

class MyApolloClient() {

    val  BASE_URL = "http://192.168.0.100:3000/graphql"
    var apolloClient: ApolloClient? = null
    var token: String = ""
    private val inst = Global.getInstance()

    init {
        val httpLoggingInterceptor = HttpLoggingInterceptor()
        httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor { chain ->
                val original = chain.request()
                val builder = original.newBuilder().method(original.method, original.body)
                builder.header("Authorization", "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1dWlkIjoiZGY4NTkzZmMtZWE2OC00ZmU4LTkzNzAtZDAzYzM3NWRjMjZlIiwidG9rZW5JZCI6MjUzMTk4OSwiaWF0IjoxNTc0ODgwNTE1LCJleHAiOjE1NzYxNzY1MTV9.K_JyrZI0nKUGOPYMfSs5SAXoVTzXXEjeL0s4CxAxTzg")
                chain.proceed(builder.build())
            }
            .build()
        apolloClient = ApolloClient.builder()
            .serverUrl(BASE_URL)
            .okHttpClient(okHttpClient)
            .build()
    }

    fun createEvent() {
        val file = File("/storage/emulated/0/DCIM/Screenshots/Screenshot_20191128-172446_Alert.jpg")
        val createEventDto = CreateEventDto.builder()
            .title("Nowy4")
            .description("OPISIK")
            .imageData(file)
            .coords((PointInput.builder()
                .x(52.303936)
                .y(16.92335).build())
            ).build()

        apolloClient!!.mutate(CreateEventMutation.builder().eventData(createEventDto)
            .build())
            .enqueue(object : ApolloCall.Callback<CreateEventMutation.Data>() {
                override fun onFailure(e: ApolloException) {
                    Log.e("ERROR", e.cause.toString())
                }

                override fun onResponse(response: Response<CreateEventMutation.Data>) {
                    Log.d("SUCCESS", response.data().toString())
                }

            })

    }

    fun fetchEvents() {
        apolloClient!!.query(
            AllEventsQuery.builder().build()).enqueue(object: ApolloCall.Callback<AllEventsQuery.Data>() {
            override fun onFailure(e: ApolloException) {

                Log.e("Źle",  e.cause.toString())
            }

            override fun onResponse(response: Response<AllEventsQuery.Data>) {
                val events = response.data()!!.events()
                Log.d("AA", "RESPONSE" + response.data()!!.events())//To change body of created functions use File | Settings | File Templates.
                for (event in events) {
                    var currentEvent = Event(
                        8,
                        UUID.fromString(event.uuid().toString()),
                        LatLng(event.coords().x(), event.coords().y()),
                        "",
                        event.title(),
                        event.description() as String,
                        1,
                        1
                        )
                    if (!inst!!.list.contains(currentEvent)) {
                        inst.list.add(currentEvent)
                    }
                    println("AAA")
                    println(inst.list)
                }
            }

        })
    }
}