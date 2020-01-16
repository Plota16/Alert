package com.plocki.alert.api.modules

import com.apollographql.apollo.ApolloCall
import com.plocki.alert.api.ApolloInstance
import com.plocki.alert.AllCategoriesQuery
import com.plocki.alert.AllEventsQuery

object CategoriesApi {

    fun fetchEvents(callback: ApolloCall.Callback<AllEventsQuery.Data>) {
        val query = AllEventsQuery.builder().build()

        ApolloInstance.query(
            query,
            callback
        )
    }

    fun fetchCategories(callback: ApolloCall.Callback<AllCategoriesQuery.Data>) {
        val query = AllCategoriesQuery.builder().build()

        ApolloInstance.query(
            query,
            callback
        )
    }
}