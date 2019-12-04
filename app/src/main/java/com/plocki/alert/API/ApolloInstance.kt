package com.plocki.alert.API

import com.apollographql.apollo.ApolloCall
import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.api.*
import com.plocki.alert.models.Global
import okhttp3.OkHttpClient

object ApolloInstance {
    var apolloClient: ApolloClient? = null
    var token: String? = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1dWlkIjoiZGY4NTkzZmMtZWE2OC00ZmU4LTkzNzAtZDAzYzM3NWRjMjZlIiwidG9rZW5JZCI6MjUzMTk4OSwiaWF0IjoxNTc0ODgwNTE1LCJleHAiOjE1NzYxNzY1MTV9.K_JyrZI0nKUGOPYMfSs5SAXoVTzXXEjeL0s4CxAxTzg"

    var BASE_URL = "http://${Global.ip}:3000/graphql"

    init {
        buildApolloClient()
    }

    fun setTokenAndStore(token: String) {
//        TODO implement setting token in store
        this.token = token
    }

    private fun getTokenIfExists(): String? {
        if (this.token != null) {
            return this.token;
        }
//        TODO implement getting token from store or returning null
        return "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1dWlkIjoiZGY4NTkzZmMtZWE2OC00ZmU4LTkzNzAtZDAzYzM3NWRjMjZlIiwidG9rZW5JZCI6MjUzMTk4OSwiaWF0IjoxNTc0ODgwNTE1LCJleHAiOjE1NzYxNzY1MTV9.K_JyrZI0nKUGOPYMfSs5SAXoVTzXXEjeL0s4CxAxTzg"
    }
    private fun buildOkHttpClient(token: String?): OkHttpClient {
        val okHttpClient = OkHttpClient.Builder()

        if (token != null) {
            okHttpClient.addInterceptor { chain ->
                val original = chain.request()
                val builder = original.newBuilder().method(original.method, original.body)
                builder.header(
                    "Authorization",
                    "Bearer ${this.token}"
                )
                chain.proceed(builder.build())
            }
        }

        return okHttpClient.build()
    }

    fun buildApolloClient() {
        var token = getTokenIfExists()
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