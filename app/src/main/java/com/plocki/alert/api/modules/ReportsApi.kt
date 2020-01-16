package com.plocki.alert.api.modules

import com.apollographql.apollo.ApolloCall
import com.plocki.alert.api.ApolloInstance
import com.plocki.alert.CreateReportMutation
import com.plocki.alert.type.CreateReportDto

object ReportsApi {
    fun createReport(createReportDto: CreateReportDto, callback: ApolloCall.Callback<CreateReportMutation.Data>) {
        val mutation = CreateReportMutation.builder().reportData(createReportDto).build()

        ApolloInstance.mutate(
            mutation,
            callback
        )
    }


}