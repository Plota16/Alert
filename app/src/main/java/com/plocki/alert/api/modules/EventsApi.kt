package com.plocki.alert.api.modules

import com.apollographql.apollo.ApolloCall
import com.plocki.alert.api.ApolloInstance
import com.plocki.alert.AllEventsQuery
import com.plocki.alert.CreateEventMutation
import com.plocki.alert.type.CreateEventDto

object EventsApi {
    fun createEvent(createEventDto: CreateEventDto, callback: ApolloCall.Callback<CreateEventMutation.Data>) {
        val mutation = CreateEventMutation.builder().eventData(createEventDto).build()

        ApolloInstance.mutate(
            mutation,
            callback
        )
    }

    fun fetchEvents(callback: ApolloCall.Callback<AllEventsQuery.Data>) {
        val query = AllEventsQuery.builder().build()

        ApolloInstance.query(
            query,
            callback
        )
    }
}