package com.plocki.alert.API.modules

import com.apollographql.apollo.ApolloCall
import com.apollographql.apollo.api.Response
import com.apollographql.apollo.exception.ApolloException
import com.google.gson.GsonBuilder
import com.plocki.alert.API.ApolloInstance
import com.plocki.alert.MyStatsQuery
import com.plocki.alert.models.Global
import com.plocki.alert.utils.HttpErrorHandler

object StatsHandler {
    fun fetchStats(){
        if (!Global.getInstance()!!.isErrorActivityOpen && Global.getInstance()!!.isUserSigned){
            ApolloInstance.buildApolloClient()
            StatsApi.fetchStats(object : ApolloCall.Callback<MyStatsQuery.Data>(){
                override fun onResponse(response: Response<MyStatsQuery.Data>) {
                    if (response.hasErrors()) {
                        val gson = GsonBuilder().create()
                        val errorMap = gson.fromJson(response.errors()[0].message(), Map::class.java)
                        HttpErrorHandler.handle(errorMap["statusCode"].toString().toFloat().toInt())
                        return
                    }
                    if(response.data() != null){
                        if(response.data()!!.myStats().reputation() != null){
                            Global.getInstance()!!.userData.reputation = response.data()!!.myStats().reputation()!!.toInt()
                        }
                        else{
                            Global.getInstance()!!.userData.reputation = 0
                        }
                        if(response.data()!!.myStats().createdEvents() != null){
                            Global.getInstance()!!.userData.createdEvents = response.data()!!.myStats().createdEvents()!!.toInt()
                        }
                        else{
                            Global.getInstance()!!.userData.createdEvents = 0
                        }
                        if(response.data()!!.myStats().likesGiven() != null){
                            Global.getInstance()!!.userData.likesGiven = response.data()!!.myStats().likesGiven()!!.toInt()
                        }
                        else{
                            Global.getInstance()!!.userData.likesGiven = 0
                        }
                        if(response.data()!!.myStats().reportsReported() != null){
                            Global.getInstance()!!.userData.reportsReported = response.data()!!.myStats().reportsReported()!!.toInt()
                        }
                        else{
                            Global.getInstance()!!.userData.reportsReported = 0
                        }
                    }
                }
                override fun onFailure(e: ApolloException) {

                    HttpErrorHandler.handle(500)
                }

            })
        }
    }
}