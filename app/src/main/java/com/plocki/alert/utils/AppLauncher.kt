package com.plocki.alert.utils

import android.app.Activity
import android.content.Intent
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import com.apollographql.apollo.ApolloCall
import com.apollographql.apollo.api.Response
import com.apollographql.apollo.exception.ApolloException
import com.plocki.alert.API.modules.FetchEventsHandler
import com.plocki.alert.API.modules.UserApi
import com.plocki.alert.CreateUserMutation
import com.plocki.alert.MyApplication
import com.plocki.alert.activities.LoginPanel
import com.plocki.alert.activities.Splash
import com.plocki.alert.models.Global
import com.plocki.alert.models.ProviderUser
import com.plocki.alert.services.FacebookService
import com.plocki.alert.services.GoogleService
import com.plocki.alert.services.TwitterService
import kotlinx.android.synthetic.main.activity_login_panel.*

object
AppLauncher {
    fun launchApp(activity:  Activity, providerUser: ProviderUser) {
        val createUser = UserApi.createUser(
            providerUser,
            object : ApolloCall.Callback<CreateUserMutation.Data>() {
                override fun onFailure(e: ApolloException) {
                }

                override fun onResponse(response: Response<CreateUserMutation.Data>) {
                    val store = Store(activity)
                    Global.getInstance()!!.userName = response.data()!!.createUser().data().username()
                    Global.getInstance()!!.userToken = response.data()!!.createUser().token().accessToken()
                    store.storeToken(Global.getInstance()!!.userToken)
                    Global.getInstance()!!.isUserSigned = true

                    FetchEventsHandler.fetchEvents(activity, true)
                }
            }
        )
    }

    fun logOut() {
        val sharedStore = Store(MyApplication.getAppContext())
        val currentActivity = Global.getInstance()!!.currentActivity!!
        sharedStore.removeToken()
        Global.getInstance()!!.isUserSigned = false
        Global.getInstance()!!.userToken = ""

        Global.getInstance()!!.isUserSigned = false
        Global.getInstance()!!.isDataChanged = false
        Global.getInstance()!!.isDataLoadedFirstTime = true
        Global.getInstance()!!.isMapCreated = true

        val googleService = GoogleService(currentActivity)
        val twitterService = TwitterService(currentActivity)
        val facebookService = FacebookService(currentActivity)

        googleService.signOut()
        twitterService.signOut()
        facebookService.signOut()
        if (currentActivity::class != LoginPanel::class || currentActivity::class != Splash::class) {
            currentActivity.runOnUiThread {
                Toast.makeText(
                    currentActivity.applicationContext,
                    "Nieautoryzowany dostęp",
                    Toast.LENGTH_SHORT
                ).show()
            }
            val intent = Intent(Global.getInstance()!!.currentActivity!!, LoginPanel::class.java)
            intent.putExtra("SHOW_WELCOME", true)
            currentActivity.startActivity(intent)
            currentActivity.finish()
            return
        }
        currentActivity.runOnUiThread {
            currentActivity.progressBar.visibility = View.INVISIBLE
            currentActivity.window.clearFlags(
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
            )
        }
    }
}