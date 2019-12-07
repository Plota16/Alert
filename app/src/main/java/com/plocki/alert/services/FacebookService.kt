package com.plocki.alert.services

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.widget.Toast
import com.facebook.*
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.plocki.alert.activities.LoginPanel
import com.plocki.alert.models.ProviderType
import com.plocki.alert.models.ProviderUser
import com.plocki.alert.utils.AppLauncher
import java.util.*
import java.util.logging.Logger

class FacebookService(context: Context,activity: Activity) {
    var context = context
    var mActivity = activity
    var callbackManager : CallbackManager? = null
    private val logger = Logger.getLogger(LoginPanel::class.java.toString())

    fun signIn() {
        callbackManager = CallbackManager.Factory.create()
        LoginManager.getInstance()
            .logInWithReadPermissions(mActivity, Arrays.asList("public_profile", "email"))
        handleSignIn()
        if (AccessToken.getCurrentAccessToken() != null) {
            getUserProfile(AccessToken.getCurrentAccessToken())
                Toast.makeText(mActivity, "ELO", Toast.LENGTH_LONG).show()
        }
        getUserProfile(AccessToken.getCurrentAccessToken())
    }

    private fun handleSignIn() {
        LoginManager.getInstance().registerCallback(callbackManager, object :
            FacebookCallback<LoginResult> {
            override fun onSuccess(result: LoginResult?) {
                val providerUser = ProviderUser(ProviderType.FACEBOOK, result!!.accessToken.token)
                AppLauncher.launchApp(context, mActivity, providerUser)
                getUserProfile(result.accessToken)
            }
            override fun onCancel() {
                Toast.makeText(mActivity, "Cancel", Toast.LENGTH_LONG).show()
            }
            override fun onError(error: FacebookException?) {
                error!!.printStackTrace()
                Toast.makeText(mActivity, "Error", Toast.LENGTH_LONG).show()
            }
        })
    }

    fun siqnOut() {
        LoginManager.getInstance().logOut()
    }

    fun getUserProfile(accessToken : AccessToken?){
        val request = GraphRequest.newMeRequest(accessToken) { `object`, response ->
            try {
                logger.info(`object`.toString())
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        val parameters = Bundle()
        parameters.putString("fields","id,name,email,picture.width(200)")
        request.parameters = parameters
        request.executeAsync()
    }
}