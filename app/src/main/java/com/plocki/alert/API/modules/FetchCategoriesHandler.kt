package com.plocki.alert.API.modules

import android.util.Log
import com.apollographql.apollo.ApolloCall
import com.apollographql.apollo.api.Response
import com.apollographql.apollo.exception.ApolloException
import com.plocki.alert.API.ApolloInstance
import com.plocki.alert.AllCategoriesQuery
import com.plocki.alert.models.Global

object FetchCategoriesHandler {
    fun fetchCategories() {
        if (!Global.getInstance()!!.isErrorActivityOpen && Global.getInstance()!!.isUserSigned) {
            ApolloInstance.buildApolloClient()
            CategoriesApi.fetchCategories(object : ApolloCall.Callback<AllCategoriesQuery.Data>() {
                override fun onFailure(e: ApolloException) {
                    println("ERROR FETCH" +  e.cause.toString())
                }

                override fun onResponse(response: Response<AllCategoriesQuery.Data>) {
                    if (response.data() != null) {
                    println("CATEGORIES " + response.data()!!.categories().toString())
                    }
                }
            })
        }
    }
}