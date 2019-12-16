package com.plocki.alert.API.modules

import android.app.Activity
import android.content.Intent
import android.util.Log
import com.apollographql.apollo.ApolloCall
import com.apollographql.apollo.api.Response
import com.apollographql.apollo.exception.ApolloException
import com.plocki.alert.API.ApolloInstance
import com.plocki.alert.AllCategoriesQuery
import com.plocki.alert.MyApplication
import com.plocki.alert.activities.MainActivity
import com.plocki.alert.models.Global

object FetchCategoriesHandler {
    fun fetchCategories(activity: Activity) {
        if (!Global.getInstance()!!.isErrorActivityOpen && Global.getInstance()!!.isUserSigned) {
            ApolloInstance.buildApolloClient()
            CategoriesApi.fetchCategories(object : ApolloCall.Callback<AllCategoriesQuery.Data>() {
                override fun onFailure(e: ApolloException) {
                    println("ERROR FETCH" +  e.cause.toString())
                }

                override fun onResponse(response: Response<AllCategoriesQuery.Data>) {
                    if (response.data() != null) {
                        println("CATEGORIES " + response.data()!!.categories().toString())
                        for (category in response.data()!!.categories()){

                        }
                    }
                    FetchEventsHandler.fetchEvents(activity)
                    val intent = Intent(MyApplication.context, MainActivity::class.java)
                    intent.putExtra("SHOW_WELCOME", true)
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    MyApplication.context!!.startActivity(intent)
                }
            })
        }
    }
}