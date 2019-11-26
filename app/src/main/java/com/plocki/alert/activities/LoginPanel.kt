package com.plocki.alert.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast

//import com.facebook.*
//import com.facebook.login.LoginManager
//import com.facebook.login.LoginResult
import com.plocki.alert.R
import kotlinx.android.synthetic.main.activity_login_panel.*
import org.json.JSONException
import java.util.*
import java.util.logging.Logger
import com.google.android.gms.auth.api.*
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.jaychang.sa.AuthCallback
import com.jaychang.sa.SocialUser
import com.facebook.*
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.twitter.sdk.android.core.*
import com.twitter.sdk.android.core.identity.TwitterAuthClient


class LoginPanel : AppCompatActivity() {


    var mail = ""
        private var callbackManager : CallbackManager? = null
    private lateinit var mAccessToken: AccessToken
    private lateinit var mGoogleSignInClient: GoogleSignInClient
    private lateinit var mTwitterAuthClient: TwitterAuthClient
    val RC_SIGN_IN = 123

    val CONSUMER_KEY = "4jkx0vsmHb2BekJoaUEgzpWC0"
    val CONSUMER_SECRET = "Yczx4FBpVhmXrhfdLFhbJ2gSFgo2mAY1OmofM9lnyljxkWDuXY"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val config = TwitterConfig.Builder(this)
            .logger(DefaultLogger(Log.DEBUG))//enable logging when app is in debug mode
            .twitterAuthConfig(
                TwitterAuthConfig(
                    CONSUMER_KEY,
                    CONSUMER_SECRET
                )
            )
            .debug(true)//enable debug mode
            .build()
        Twitter.initialize(config)

        setContentView(R.layout.activity_login_panel)
        //TWITTER
        mTwitterAuthClient = TwitterAuthClient()
//        twitter_login_button.setOnClickListener(View.OnClickListener {
//            if (getTwitterSession() == null) {
//                handleTwitterSignInResult()
//            } else {//if user is already authenticated direct call fetch twitter email api
//                fetchTwitterEmail(getTwitterSession())
//            }
//        })
//
////        //GOOGLE
//        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
//            .requestEmail()
//            .build()
//        val mGoogleSignInClient = GoogleSignIn.getClient(this, gso)
//        google_login_button.setOnClickListener{
//            val signInIntent = mGoogleSignInClient.signInIntent
//            startActivityForResult(signInIntent, RC_SIGN_IN)
//
//        }
//        val acct = GoogleSignIn.getLastSignedInAccount(this)
//        if (acct != null) {
//            println(acct.idToken)
//        }
////
////        //FACEBOOK
//        face_login_button.setOnClickListener(View.OnClickListener {
//            callbackManager = CallbackManager.Factory.create()
//            LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile", "email"))
//            handleFacebookSignInResult()
//            if(AccessToken.getCurrentAccessToken()!=null){
//                getUserProfile(AccessToken.getCurrentAccessToken())
//                Toast.makeText(this@LoginPanel, mail, Toast.LENGTH_LONG).show()
//            }
//        })
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        callbackManager?.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleGoogleSignInResult(task)
        }

        if (mTwitterAuthClient != null) {
            mTwitterAuthClient!!.onActivityResult(requestCode, resultCode, data)
        }
    }


    val logger = Logger.getLogger(LoginPanel::class.java.toString())

    private fun handleTwitterSignInResult() {
        mTwitterAuthClient!!.authorize(this, object : Callback<TwitterSession>() {
            override fun success(twitterSessionResult: Result<TwitterSession>) {
                Toast.makeText(this@LoginPanel, "Success", Toast.LENGTH_SHORT).show()
                val twitterSession = twitterSessionResult.data
                fetchTwitterEmail(twitterSession)
            }
            override fun failure(e: TwitterException) {
                Toast.makeText(this@LoginPanel, "Failure", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun handleFacebookSignInResult() {
        LoginManager.getInstance().registerCallback(callbackManager, object : FacebookCallback<LoginResult>
        {
            override fun onSuccess(result: LoginResult?) {
                mAccessToken = result!!.getAccessToken()
                println(result.accessToken.expires)
                println(result.accessToken.token)
                println(result.accessToken.lastRefresh)
                println(result.accessToken.userId)
                println(result.accessToken.source)

                println("LOGUJE FACE")
                getUserProfile(mAccessToken)
            }
            override fun onCancel() {
                Toast.makeText(this@LoginPanel, "Cancel", Toast.LENGTH_LONG).show()
            }
            override fun onError(error: FacebookException?) {
                error!!.printStackTrace()
                Toast.makeText(this@LoginPanel, "Error", Toast.LENGTH_LONG).show()
            }

        })
    }

    private fun handleGoogleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            if (completedTask.isComplete) {
                logger.info("COMPLETE")
                completedTask.addOnCanceledListener {
                    Toast.makeText(this@LoginPanel, "Cancel", Toast.LENGTH_LONG).show()
                }
                completedTask.addOnSuccessListener {
                    Toast.makeText(this@LoginPanel, "Success", Toast.LENGTH_LONG).show()
            val account = completedTask.getResult()
        val idToken = account!!.getIdToken()
                    println(idToken)

//                    logger.info(account!!.get)
                    logger.info(account!!.email)
                    logger.info(account!!.displayName)
                    logger.info(account!!.id)
                    logger.info(account!!.account.toString())
                    logger.info(account!!.isExpired.toString())




                }
                completedTask.addOnFailureListener {
                    Toast.makeText(this@LoginPanel, "Fail", Toast.LENGTH_LONG).show()

                }
            }
        } catch (e: ApiException) {
            println("Błąd")
            Toast.makeText(this@LoginPanel, "Fail", Toast.LENGTH_LONG).show()
        }
    }

    fun getUserProfile(accessToken : AccessToken?){
        println("LOGUJEEEEE FACE")
        val request = GraphRequest.newMeRequest(accessToken) { `object`, response ->
            try {
                logger.info(accessToken.toString())
                //here is the data that you want
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

    private fun getTwitterSession(): TwitterSession? {
        return TwitterCore.getInstance().sessionManager.activeSession
    }

    fun fetchTwitterEmail(twitterSession: TwitterSession?) {
        mTwitterAuthClient?.requestEmail(twitterSession, object : Callback<String>() {
            override fun success(result: Result<String>) {
                //here it will give u only email and rest of other information u can get from TwitterSession

//                Log.d("a", result.response.toString())
                Log.d("a", "twitterLogin:userName" + twitterSession!!.userName)
//                println("toks " + result.)

                var userId = twitterSession!!.userId
                var username = twitterSession!!.userName
                var email = result.data
                var token = twitterSession.userId.toString()
                var str = "Now you are successfully login with twitter \n\n"
                var tokenStr = ""
                var usernameStr = ""
                if (token != null || token != "") {
                    tokenStr = "User Id : " + token + "\n\n"
                }
                if (username != null || username != "") {
                    usernameStr = "Username : " + username + "\n\n"
                }
//                println("" + str + tokenStr + usernameStr)

            }
            override fun failure(exception: TwitterException) {
                Toast.makeText(
                    this@LoginPanel,
                    "Failed to authenticate. Please try again.",
                    Toast.LENGTH_SHORT
                )
                    .show()
            }
        })
    }

    fun connectGoogle(view: View) {
        val scopes = Arrays.asList("email")
        com.jaychang.sa.google.SimpleAuth.connectGoogle(scopes, object : AuthCallback {
            override fun onSuccess(socialUser: SocialUser) {
                println(socialUser.accessToken.toString())
                println(socialUser.fullName)
                println(socialUser.userId)
                println(socialUser.username)
                println(socialUser.email)
            }

            override fun onError(error: Throwable) {
                Toast.makeText(this@LoginPanel, "Błąd logowania", Toast.LENGTH_SHORT)
            }

            override fun onCancel() {
                Toast.makeText(this@LoginPanel, "Błąd logowania", Toast.LENGTH_SHORT)

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


}
