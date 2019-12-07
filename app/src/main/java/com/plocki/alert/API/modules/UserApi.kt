package com.plocki.alert.API.modules

import com.apollographql.apollo.ApolloCall
import com.plocki.alert.API.ApolloInstance
import com.plocki.alert.AllEventsQuery
import com.plocki.alert.CreateEventMutation
import com.plocki.alert.CreateUserMutation
import com.plocki.alert.WhoAmIQuery
import com.plocki.alert.models.Event
import com.plocki.alert.models.ProviderUser
import com.plocki.alert.type.CreateOAuthUserDto

object UserApi {
    fun createUser(providerUser: ProviderUser, callback: ApolloCall.Callback<CreateUserMutation.Data>) {
        val createOAuthUserDto = providerUser.createOAuthUserDto()
        val mutation = CreateUserMutation.builder().userData(createOAuthUserDto).build()
        ApolloInstance.mutate(
            mutation,
            callback
        )
    }
    fun whoAmI(callback: ApolloCall.Callback<WhoAmIQuery.Data>) {
        val query = WhoAmIQuery.builder().build()

        ApolloInstance.query(
            query,
            callback
        )
    }
}