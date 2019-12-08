package com.plocki.alert.utils

import android.app.Activity
import android.content.Context
import android.util.Log
import com.apollographql.apollo.ApolloCall
import com.apollographql.apollo.api.Response
import com.apollographql.apollo.exception.ApolloException
import com.plocki.alert.API.modules.FetchEventsHandler
import com.plocki.alert.API.modules.UserApi
import com.plocki.alert.CreateUserMutation
import com.plocki.alert.models.Global
import com.plocki.alert.models.ProviderUser

object
AppLauncher {
    fun launchApp(context: Context, activity:  Activity, providerUser: ProviderUser) {
        val createUser = UserApi.createUser(
            providerUser,
            object : ApolloCall.Callback<CreateUserMutation.Data>() {
                override fun onFailure(e: ApolloException) {
                    Log.e("ERROR", e.cause.toString())
                }

                override fun onResponse(response: Response<CreateUserMutation.Data>) {
                    Log.d("Apollo SUCCESS", response.data().toString())
                    val store = Store(context)
                    Global.getInstance()!!.username = response.data()!!.createUser().data().username()
                    Global.getInstance()!!.token = response.data()!!.createUser().token().accessToken()
                    store.storeValue("token",Global.getInstance()!!.token)
                    Global.getInstance()!!.logged = true

                    FetchEventsHandler.fetchEvents(activity, true)
                }
            }
        )
    }
}