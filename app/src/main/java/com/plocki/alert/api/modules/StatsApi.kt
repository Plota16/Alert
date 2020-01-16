package com.plocki.alert.api.modules

import com.apollographql.apollo.ApolloCall
import com.plocki.alert.api.ApolloInstance
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