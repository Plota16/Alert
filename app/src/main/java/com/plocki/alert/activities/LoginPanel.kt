package com.plocki.alert.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast

import com.facebook.*
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
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


class LoginPanel : AppCompatActivity() {

    var mail = ""
    private var callbackManager : CallbackManager? = null
    private lateinit var mAccessToken: AccessToken
    private lateinit var  mGoogleSignInClient: GoogleSignInClient
    val RC_SIGN_IN = 123

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_panel)
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()
        val mGoogleSignInClient = GoogleSignIn.getClient(this, gso)
        google_login_button.setOnClickListener{
            val signInIntent = mGoogleSignInClient.signInIntent
            startActivityForResult(signInIntent, RC_SIGN_IN)

        }
        val acct = GoogleSignIn.getLastSignedInAccount(this)
        if (acct != null) {
            println(acct.idToken)
        }


        face_login_button.setOnClickListener(View.OnClickListener {
            callbackManager = CallbackManager.Factory.create()

            LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile", "email"))
            LoginManager.getInstance().registerCallback(callbackManager, object : FacebookCallback<LoginResult>
            {
                override fun onSuccess(result: LoginResult?) {
                    mAccessToken = result!!.getAccessToken()
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

            if(AccessToken.getCurrentAccessToken()!=null){
                getUserProfile(AccessToken.getCurrentAccessToken())
                Toast.makeText(this@LoginPanel, mail, Toast.LENGTH_LONG).show()
            }
        })


    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        callbackManager?.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleSignInResult(task)
        }
    }
    val logger = Logger.getLogger(LoginPanel::class.java.toString())

    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        println("orzed")
        try {
            val account = completedTask.result
            println(account)
            logger.info(account.toString())
        } catch (e: ApiException) {
    println("nie")

        }
    }

    fun getUserProfile(accessToken : AccessToken?){
        val request = GraphRequest.newMeRequest(accessToken) { `object`, response ->
            try {
                //here is the data that you want
                logger.info("FBLOGIN_JSON_RES\n" +  `object`.toString())

                if (`object`.has("id")) {
                    logger.warning(`object`.getString("email")
                    )
                } else {
                    logger.warning("FBLOGIN_FAILD")
                }

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        val parameters = Bundle()
        parameters.putString("fields", "name,email,id,picture.type(large)")
        request.parameters = parameters
        request.executeAsync()

//        val request = GraphRequest.newMeRequest(token) { `object`, response ->
//            try {
//                mail = response.getJSONObject().get("email").toString()
//
//            } catch (e: JSONException){
//                e.printStackTrace()
//            }
//        }
//
//        val parameters = Bundle()
//        parameters.putString("fields","id,name,email,picture.width(200)")
//        request.parameters = parameters
//        request.executeAsync()

    }
}
