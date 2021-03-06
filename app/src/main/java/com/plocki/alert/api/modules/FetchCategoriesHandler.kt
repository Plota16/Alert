package com.plocki.alert.api.modules

import android.app.Activity
import android.util.Log
import com.apollographql.apollo.ApolloCall
import com.apollographql.apollo.api.Response
import com.apollographql.apollo.exception.ApolloException
import com.google.gson.GsonBuilder
import com.plocki.alert.api.ApolloInstance
import com.plocki.alert.AllCategoriesQuery
import com.plocki.alert.models.Category
import com.plocki.alert.models.Global
import com.plocki.alert.utils.HttpErrorHandler

object FetchCategoriesHandler {
    fun fetchCategories(activity: Activity) {
        if (!Global.getInstance()!!.isErrorActivityOpen && Global.getInstance()!!.isUserSigned) {
            ApolloInstance.buildApolloClient()
            CategoriesApi.fetchCategories(object : ApolloCall.Callback<AllCategoriesQuery.Data>() {
                override fun onFailure(e: ApolloException) {
                    HttpErrorHandler.handle(500)
                }


                override fun onResponse(response: Response<AllCategoriesQuery.Data>) {
                    if (response.hasErrors()) {
                        Log.e("ERROR ", response.errors()[0].customAttributes()["statusCode"].toString())
                        val gson = GsonBuilder().create()
                        val errorMap = gson.fromJson(response.errors()[0].message(), Map::class.java)
                        HttpErrorHandler.handle(errorMap["statusCode"].toString().toFloat().toInt())
                        return
                    }

                    if (response.data() != null) {
                        Global.getInstance()!!.categoryList.clear()
                        Global.getInstance()!!.filterHashMap.clear()
                        Global.getInstance()!!.titleUUIDHashMap.clear()
                        println("CATEGORIES " + response.data()!!.categories().toString())
                        for (category in response.data()!!.categories()){
                            Global.getInstance()!!.categoryHashMap[category.uuid().toString()] =
                                Category(
                                    category.uuid().toString(),
                                    category.title(),
                                    category.color()
                                )
                            Global.getInstance()!!.categoryList.add(category.title())
                            Global.getInstance()!!.filterHashMap[category.title()] = true
                            Global.getInstance()!!.titleUUIDHashMap[category.title()] = category.uuid().toString()
                        }
                        FetchEventsHandler.fetchEvents(activity = activity, startMain = true)
                    }
                }
            })
        }
    }
}