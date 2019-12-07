package com.plocki.alert.services

import android.app.Activity
import android.content.Context
import android.util.Log
import android.widget.Toast
import com.plocki.alert.models.ProviderType
import com.plocki.alert.models.ProviderUser
import com.plocki.alert.utils.AppLauncher
import com.twitter.sdk.android.core.*
import com.twitter.sdk.android.core.identity.TwitterAuthClient

class TwitterService(context: Context, activity: Activity) {
    var context = context
    var mActivity: Activity = activity
    var mTwitterAuthClient: TwitterAuthClient = TwitterAuthClient()


    fun buildConfiguration() : TwitterConfig {
        val CONSUMER_KEY = "4jkx0vsmHb2BekJoaUEgzpWC0"
        val CONSUMER_SECRET = "Yczx4FBpVhmXrhfdLFhbJ2gSFgo2mAY1OmofM9lnyljxkWDuXY"

        return TwitterConfig.Builder(mActivity)
            .logger(DefaultLogger(Log.DEBUG))//enable logging when app is in debug mode
            .twitterAuthConfig(
                TwitterAuthConfig(
                    CONSUMER_KEY,
                    CONSUMER_SECRET
                )
            )
            .debug(true)//enable debug mode
            .build()
    }

    fun signIn() {
        mTwitterAuthClient.authorize(mActivity, object : Callback<TwitterSession>() {
            override fun success(twitterSessionResult: Result<TwitterSession>) {
                val twitterSession = twitterSessionResult.data
                fetchTwitterEmail(twitterSession)
            }

            override fun failure(e: TwitterException) {
            }
        })
    }

    fun signOut() {
        val twitterSession = TwitterCore.getInstance().sessionManager.clearActiveSession()
    }

    fun fetchTwitterEmail(twitterSession: TwitterSession?) {
        //TODO Na serwerze nie ma Twittera, więc później trzeba zmienić
        mTwitterAuthClient.requestEmail(twitterSession, object : Callback<String>() {
            override fun success(result: Result<String>) {
                Log.d("TWITTER", "twitterLogin:token" + twitterSession!!.authToken.token)
                Log.d("TWITTER", "twitterLogin:secret" + twitterSession.authToken.secret)
                Log.d("TWITTER", "twitterLogin:secret" + twitterSession.userId)
                val providerUser = ProviderUser(ProviderType.GOOGLE, twitterSession.authToken.token)
                AppLauncher.launchApp(context, mActivity, providerUser)

            }
            override fun failure(exception: TwitterException) {
                Toast.makeText(
                    mActivity,
                    "Failed to authenticate. Please try again.",
                    Toast.LENGTH_SHORT
                )
                    .show()
            }
        })
    }
}