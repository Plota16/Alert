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
import com.plocki.alert.models.Category
import com.plocki.alert.models.Global
import java.util.*

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
                    }


                }
            })
        }
    }
}