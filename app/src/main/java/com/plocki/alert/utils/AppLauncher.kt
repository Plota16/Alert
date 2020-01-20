package com.plocki.alert.utils

import android.app.Activity
import android.content.Intent
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import com.apollographql.apollo.ApolloCall
import com.apollographql.apollo.api.Response
import com.apollographql.apollo.exception.ApolloException
import com.google.gson.GsonBuilder
import com.google.gson.JsonSyntaxException
import com.plocki.alert.api.modules.FetchEventsHandler
import com.plocki.alert.api.modules.UserApi
import com.plocki.alert.CreateUserMutation
import com.plocki.alert.MyApplication
import com.plocki.alert.activities.LoginPanel
import com.plocki.alert.models.Global
import com.plocki.alert.models.ProviderUser
import com.plocki.alert.services.FacebookService
import com.plocki.alert.services.GoogleService
import com.plocki.alert.services.TwitterService
import kotlinx.android.synthetic.main.activity_login_panel.*

object
AppLauncher {
    fun launchApp(activity:  Activity, providerUser: ProviderUser) {
        UserApi.createUser(
            providerUser,
            object : ApolloCall.Callback<CreateUserMutation.Data>() {
                override fun onFailure(e: ApolloException) {
                }

                override fun onResponse(response: Response<CreateUserMutation.Data>) {
                    if (response.hasErrors()) {
                        Log.e("ERROR ", response.errors()[0].customAttributes()["statusCode"].toString())
                        val gson = GsonBuilder().create()
                        try {
                            val errorMap =
                                gson.fromJson(response.errors()[0].message(), Map::class.java)
                            HttpErrorHandler.handle(errorMap["statusCode"].toString().toFloat().toInt())
                        } catch (e: JsonSyntaxException) {

                            HttpErrorHandler.handle(500)
                            Log.e("ERROR ","Błąd bazy danych")

                        }
                        return
                    }
                    val store = Store(activity)
                    store.storeProvider(providerUser.providerType.toString())
                    Global.getInstance()!!.userName = response.data()!!.createUser().data().username()
                    Global.getInstance()!!.userToken = response.data()!!.createUser().token().accessToken()
                    println("User Token: " +  response.data()!!.createUser().token())
                    store.storeToken(Global.getInstance()!!.userToken)
                    Global.getInstance()!!.isUserSigned = true

                    FetchEventsHandler.fetchEvents(activity, true)
                }
            }
        )
    }

    fun logOut(logOutUsingButton: Boolean = false) {
        val sharedStore = Store(MyApplication.getAppContext())
        val currentActivity = Global.getInstance()!!.currentActivity!!
        sharedStore.removeToken()
        sharedStore.removeProvider()
        Global.getInstance()!!.resetStateVariables()

        val googleService = GoogleService(currentActivity)
        val twitterService = TwitterService(currentActivity)
        val facebookService = FacebookService(currentActivity)

        googleService.signOut()
        twitterService.signOut()
        facebookService.signOut()
        if (!logOutUsingButton) {
            currentActivity.runOnUiThread {
                Toast.makeText(
                    currentActivity.applicationContext,
                    "Nieautoryzowany dostęp",
                    Toast.LENGTH_SHORT
                ).show()
            }

        }

        currentActivity.runOnUiThread {
            currentActivity.progressBar.visibility = View.INVISIBLE
            currentActivity.window.clearFlags(
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
            )
        }
        val intent = Intent(Global.getInstance()!!.currentActivity!!, LoginPanel::class.java)
        intent.putExtra("SHOW_WELCOME", true)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        currentActivity.startActivity(intent)
        currentActivity.finish()
    }
}