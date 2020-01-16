package com.plocki.alert.api.modules


import com.apollographql.apollo.ApolloCall
import com.plocki.alert.api.ApolloInstance
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