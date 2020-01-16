package com.plocki.alert.api

import com.apollographql.apollo.ApolloCall
import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.api.*
import com.plocki.alert.models.Global
import okhttp3.OkHttpClient

object ApolloInstance {
    private var apolloClient: ApolloClient? = null
    private var BASE_URL = "https://${Global.ip}/graphql"

    init {
        buildApolloClient()
    }

    private fun buildOkHttpClient(token: String?): OkHttpClient {
        val okHttpClient = OkHttpClient.Builder()

        if (token != null) {
            okHttpClient.addInterceptor { chain ->
                val original = chain.request()
                val builder = original.newBuilder().method(original.method, original.body)
                builder.header(
                    "Authorization",
                    "Bearer $token"
                )
                chain.proceed(builder.build())
            }
        }

        return okHttpClient.build()
    }

    fun buildApolloClient() {
        val token = Global.getInstance()!!.userToken
        val okHttpClient = buildOkHttpClient(token)

        apolloClient = ApolloClient.builder()
            .serverUrl(BASE_URL)
            .okHttpClient(okHttpClient)
            .build()
    }

    fun <OperationData: Operation.Data, OperationVariables: Operation.Variables> query(
        query: Query<OperationData, OperationData, OperationVariables>,
        callback: ApolloCall.Callback<OperationData>
    ) {
        apolloClient!!.query(query).enqueue(callback)
    }

    fun <OperationData: Operation.Data, OperationVariables: Operation.Variables> mutate(
        mutation: Mutation<OperationData, OperationData, OperationVariables>,
        callback: ApolloCall.Callback<OperationData>
    ) {
        apolloClient!!.mutate(mutation).enqueue(callback)
    }
}