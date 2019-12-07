package com.plocki.alert.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.apollographql.apollo.ApolloCall
import com.apollographql.apollo.api.Response
import com.apollographql.apollo.exception.ApolloException
import kotlinx.android.synthetic.main.activity_login_panel.*
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.plocki.alert.API.modules.UserApi
import com.plocki.alert.MyApplication
import com.plocki.alert.services.FacebookService
import com.plocki.alert.services.GoogleService
import com.plocki.alert.services.TwitterService
import com.twitter.sdk.android.core.*
import com.plocki.alert.R
import com.plocki.alert.WhoAmIQuery
import com.plocki.alert.models.Global
import com.plocki.alert.utils.Store
import java.lang.Exception


class LoginPanel : AppCompatActivity() {

    private lateinit var googleService: GoogleService
    private lateinit var twitterService: TwitterService
    private lateinit var facebookService: FacebookService

    override fun onCreate(savedInstanceState: Bundle?) {
        val store = Store(this)
        try {
            Global.getInstance()!!.token = store.retrieveValue("token")
        }catch (ex : Exception){
            ex.printStackTrace()
        }

        if(Global.getInstance()!!.token != null){
            UserApi.whoAmI(object : ApolloCall.Callback<WhoAmIQuery.Data>(){
                override fun onResponse(response: Response<WhoAmIQuery.Data>) {
                    val whoAmI = response.data()!!.whoAmI()
                    Global.getInstance()!!.username = whoAmI.username()
                    val intent = Intent(MyApplication.context, MainActivity::class.java)
                    intent.putExtra("SHOW_WELCOME", true)
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    Global.getInstance()!!.logged = true
                    MyApplication.context!!.startActivity(intent)
                }

                override fun onFailure(e: ApolloException) {
                    Global.getInstance()!!.token = null
                    val sharedstore = Store(this@LoginPanel)
                    sharedstore.removeValue("token")
                }

            })

        }




        googleService = GoogleService(this, this)
        twitterService = TwitterService(this, this)
        facebookService = FacebookService(this, this)

        Twitter.initialize(twitterService.buildConfiguration())

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_panel)

        //TWITTER
        twitter_login_button.setOnClickListener {
            progressBar.visibility = View.VISIBLE
            twitterService.signIn()
        }

        //GOOGLE
        google_login_button.setOnClickListener {
            progressBar.visibility = View.VISIBLE
            val signInIntent = googleService.mGoogleSignInClient.signInIntent
            startActivityForResult(signInIntent, 9002)
        }

        //FACEBOOK
        face_login_button.setOnClickListener(View.OnClickListener {
            progressBar.visibility = View.VISIBLE
            facebookService.signIn()

        })

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        facebookService.callbackManager?.onActivityResult(requestCode, resultCode, data)
        twitterService.mTwitterAuthClient.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 9002) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            googleService.handleGoogleSignInResult(task)
        }
    }

    fun logoClicked(view: View) {
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("SHOW_WELCOME", true)
        this.startActivity(intent)
    }

    fun askForName(){

    }

}
