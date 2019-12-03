package com.plocki.alert.API.modules

import com.apollographql.apollo.ApolloCall
import com.plocki.alert.API.ApolloInstance
import com.plocki.alert.AllEventsQuery
import com.plocki.alert.CreateEventMutation
import com.plocki.alert.models.Event

object EventsApi {
    fun createEvent(event: Event, callback: ApolloCall.Callback<CreateEventMutation.Data>) {
        println(event)
        val createEventDto = event.createEventDto()
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