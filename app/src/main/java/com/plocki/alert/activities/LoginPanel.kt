package com.plocki.alert.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.facebook.*
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.plocki.alert.R
import kotlinx.android.synthetic.main.activity_login_panel.*
import org.json.JSONException
import java.util.*


class LoginPanel : AppCompatActivity() {

    var mail = ""
    private var callbackManager : CallbackManager? = null
    private lateinit var mAccessToken: AccessToken


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_panel)

        callbackManager = CallbackManager.Factory.create()



        LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile"));

        face_login_button.registerCallback(callbackManager, object : FacebookCallback<LoginResult>
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

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        callbackManager?.onActivityResult(requestCode, resultCode, data)
    }


    fun getUserProfile(token : AccessToken?){
        val request = GraphRequest.newMeRequest(token) { `object`, response ->
            try {
                mail = response.getJSONObject().get("email").toString()
                val tmp = ""

            } catch (e: JSONException){
                e.printStackTrace()
            }
        }

        var parameters = Bundle()
        parameters.putString("fields","id,name,email,picture.width(200)")
        request.parameters = parameters
        request.executeAsync()

    }
}
