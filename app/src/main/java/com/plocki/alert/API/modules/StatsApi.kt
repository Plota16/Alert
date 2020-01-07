package com.plocki.alert.API.modules

import com.apollographql.apollo.ApolloCall
import com.plocki.alert.API.ApolloInstance
import com.plocki.alert.MyStatsQuery

object StatsApi {
    fun fetchStats(callback: ApolloCall.Callback<MyStatsQuery.Data>) {
        val query = MyStatsQuery.builder().build()

        ApolloInstance.query(
            query,
            callback
        )
    }
}