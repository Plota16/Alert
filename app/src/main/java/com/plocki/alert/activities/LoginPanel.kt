package com.plocki.alert.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import kotlinx.android.synthetic.main.activity_login_panel.*
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.plocki.alert.services.FacebookService
import com.plocki.alert.services.GoogleService
import com.plocki.alert.services.TwitterService
import com.twitter.sdk.android.core.*
import android.content.pm.PackageManager
import android.util.Base64
import android.util.Log
import com.plocki.alert.R
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException


class LoginPanel : AppCompatActivity() {

    lateinit var googleService: GoogleService
    lateinit var twitterService: TwitterService
    lateinit var facebookService: FacebookService

    override fun onCreate(savedInstanceState: Bundle?) {
        googleService = GoogleService(this)
        twitterService = TwitterService(this)
        facebookService = FacebookService(this)

        Twitter.initialize(twitterService.buildConfiguration())

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_panel)

        //TWITTER
        twitter_login_button.setOnClickListener {
            twitterService.signIn()
        }

        //GOOGLE
        google_login_button.setOnClickListener {
            val signInIntent = googleService.mGoogleSignInClient.signInIntent
            startActivityForResult(signInIntent, 9002)
        }

        //FACEBOOK
        face_login_button.setOnClickListener(View.OnClickListener {
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


}
