package com.plocki.alert.API.modules

import com.apollographql.apollo.ApolloCall
import com.plocki.alert.API.ApolloInstance
import com.plocki.alert.AllCategoriesQuery
import com.plocki.alert.AllEventsQuery
import com.plocki.alert.CreateEventMutation
import com.plocki.alert.models.Event

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