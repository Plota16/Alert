package com.plocki.alert.api.modules

import com.apollographql.apollo.ApolloCall
import com.plocki.alert.api.ApolloInstance
import com.plocki.alert.CreateUserMutation
import com.plocki.alert.DeleteUserMutation
import com.plocki.alert.WhoAmIQuery
import com.plocki.alert.models.ProviderUser

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
    fun deleteUser(callback: ApolloCall.Callback<DeleteUserMutation.Data>) {
        val mutation = DeleteUserMutation.builder().build()

        ApolloInstance.mutate(
            mutation,
            callback
        )
    }
}