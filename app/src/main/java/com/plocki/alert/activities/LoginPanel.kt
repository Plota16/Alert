package com.plocki.alert.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.apollographql.apollo.ApolloCall
import com.apollographql.apollo.api.Response
import com.apollographql.apollo.exception.ApolloException
import com.jaychang.sa.AuthCallback
import com.jaychang.sa.SocialUser
import com.plocki.alert.API.modules.UserApi
import com.plocki.alert.CreateUserMutation
import com.plocki.alert.R
import com.plocki.alert.models.ProviderType
import com.plocki.alert.models.ProviderUser
import kotlinx.android.synthetic.main.activity_login_panel.*
import java.util.*


class LoginPanel : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_panel)
    }

    fun connectGoogle(view: View) {
        com.jaychang.sa.google.SimpleAuth.disconnectGoogle()
        val scopes = Arrays.asList("email")
        com.jaychang.sa.google.SimpleAuth.connectGoogle(scopes, object : AuthCallback {
            override fun onSuccess(socialUser: SocialUser) {
                println(socialUser.accessToken.toString())
                println(socialUser.fullName)
                println(socialUser.userId)
                println(socialUser.username)
                println(socialUser.email)
                loginToApp(ProviderUser(ProviderType.GOOGLE, socialUser.accessToken))
            }

            override fun onError(error: Throwable) {
            }

            override fun onCancel() {

            }
        })
    }

    fun connectFacebook(view: View) {
        val scopes = Arrays.asList("user_birthday", "user_friends")
        com.jaychang.sa.facebook.SimpleAuth.connectFacebook(scopes, object : AuthCallback {
            override fun onSuccess(socialUser: SocialUser) {
                println(socialUser.accessToken.toString())
                println(socialUser.fullName)
                println(socialUser.userId)
                println(socialUser.username)
                println(socialUser.email)
                loginToApp(ProviderUser(ProviderType.FACOBOOK, socialUser.accessToken))

            }

            override fun onError(error: Throwable) {
                Toast.makeText(this@LoginPanel, "Błąd logowania", Toast.LENGTH_SHORT)
            }

            override fun onCancel() {
                Toast.makeText(this@LoginPanel, "Błąd logowania", Toast.LENGTH_SHORT)
            }
        })
    }

    fun connectTwitter(view: View) {
        com.jaychang.sa.twitter.SimpleAuth.connectTwitter(object : AuthCallback {
            override fun onSuccess(socialUser: SocialUser) {
                println(socialUser.accessToken.toString())
                println(socialUser.fullName)
                println(socialUser.userId)
                println(socialUser.username)
                println(socialUser.email)
                loginToApp(ProviderUser(ProviderType.TWITTER, socialUser.accessToken))

            }

            override fun onError(error: Throwable) {
                Toast.makeText(this@LoginPanel, "Błąd logowania", Toast.LENGTH_SHORT)
            }

            override fun onCancel() {
                Toast.makeText(this@LoginPanel, "Błąd logowania", Toast.LENGTH_SHORT)
            }
        })
    }

    fun logoClicked(v: View){
        logIn()
    }

    fun logIn(){
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("SHOW_WELCOME", true)
        startActivity(intent)
    }

    fun loginToApp(providerUser: ProviderUser) {
        runOnUiThread { progressBar.visibility = View.VISIBLE }
            val createUser = UserApi.createUser(
                providerUser,
                object : ApolloCall.Callback<CreateUserMutation.Data>() {
                    override fun onFailure(e: ApolloException) {
                        progressBar.visibility = View.INVISIBLE
                        Log.e("ERROR", e.cause.toString())
                    }

                    override fun onResponse(response: Response<CreateUserMutation.Data>) {
                        Log.d("SUCCESS", response.data().toString())
                        runOnUiThread { progressBar.visibility = View.INVISIBLE }
                        logIn()
                        finish()
                    }
                }
            )

}


}
