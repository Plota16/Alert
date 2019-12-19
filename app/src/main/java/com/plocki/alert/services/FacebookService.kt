package com.plocki.alert.services

import android.app.Activity
import android.os.Bundle
import android.widget.Toast
import com.facebook.*
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.plocki.alert.activities.LoginPanel
import com.plocki.alert.models.ProviderType
import com.plocki.alert.models.ProviderUser
import com.plocki.alert.utils.AppLauncher
import java.util.logging.Logger

class FacebookService(activity: Activity) {
    var mActivity = activity
    var callbackManager : CallbackManager? = null
    private val logger = Logger.getLogger(LoginPanel::class.java.toString())

    fun signIn() {
        callbackManager = CallbackManager.Factory.create()
        LoginManager.getInstance()
            .logInWithReadPermissions(mActivity, listOf("public_profile", "email"))
        handleSignIn()
        if (AccessToken.getCurrentAccessToken() != null) {
            getUserProfile(AccessToken.getCurrentAccessToken())
        }
        getUserProfile(AccessToken.getCurrentAccessToken())
    }

    private fun handleSignIn() {
        LoginManager.getInstance().registerCallback(callbackManager, object :
            FacebookCallback<LoginResult> {
            override fun onSuccess(result: LoginResult?) {
                val providerUser = ProviderUser(ProviderType.FACEBOOK, result!!.accessToken.token)
                AppLauncher.launchApp(mActivity, providerUser)
                getUserProfile(result.accessToken)
            }
            override fun onCancel() {
                //TODO CZY COS Z TYM ROBIć
//                Toast.makeText(mActivity, "Cancel", Toast.LENGTH_LONG).show()
            }
            override fun onError(error: FacebookException?) {
                error!!.printStackTrace()
                Toast.makeText(mActivity, "Nie można połączyć się przy pomocy Facebook", Toast.LENGTH_LONG).show()
                throw error

            }
        })
    }

    fun signOut() {
        LoginManager.getInstance().logOut()
    }

    fun getUserProfile(accessToken : AccessToken?){
        val request = GraphRequest.newMeRequest(accessToken) { `object`, _ ->
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