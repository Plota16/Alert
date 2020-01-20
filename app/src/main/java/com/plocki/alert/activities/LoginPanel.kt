package com.plocki.alert.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.plocki.alert.R
import com.plocki.alert.models.Global
import com.plocki.alert.services.FacebookService
import com.plocki.alert.services.GoogleService
import com.plocki.alert.services.TwitterService
import com.twitter.sdk.android.core.Twitter
import kotlinx.android.synthetic.main.activity_login_panel.*
import kotlin.system.exitProcess

class LoginPanel : AppCompatActivity() {

    private lateinit var googleService: GoogleService
    private lateinit var twitterService: TwitterService
    private lateinit var facebookService: FacebookService

    override fun onCreate(savedInstanceState: Bundle?) {
        Global.getInstance()!!.currentActivity = this

        googleService = GoogleService(this)
        twitterService = TwitterService(this)
        facebookService = FacebookService(this)

        Twitter.initialize(twitterService.buildConfiguration())

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_panel)

        //TWITTER
        twitter_login_button.setOnClickListener {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
            progressBar.visibility = View.VISIBLE
            twitterService.signIn()

        }

        //GOOGLE
        google_login_button.setOnClickListener {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
            progressBar.visibility = View.VISIBLE
            val signInIntent = googleService.mGoogleSignInClient.signInIntent
            startActivityForResult(signInIntent, 9002)
        }

        //FACEBOOK
        face_login_button.setOnClickListener {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
            progressBar.visibility = View.VISIBLE

            facebookService.signIn()


        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(resultCode == 0){
            progressBar.visibility = View.INVISIBLE
            window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
            return
        }
        facebookService.callbackManager?.onActivityResult(requestCode, resultCode, data)
        twitterService.mTwitterAuthClient.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 9002) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            googleService.handleGoogleSignInResult(task)
        }
    }

    override fun onBackPressed() {
        exitProcess(0)
    }

}
