package com.plocki.alert.API.modules


import com.apollographql.apollo.ApolloCall
import com.plocki.alert.API.ApolloInstance
import com.plocki.alert.AllReportCategoriesQuery

object ReportCategoriesApi {
    fun fetchReportCategories(callback: ApolloCall.Callback<AllReportCategoriesQuery.Data>) {
        val query = AllReportCategoriesQuery.builder().build()

        ApolloInstance.query(
            query,
            callback
        )
    }
}