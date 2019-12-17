package com.plocki.alert.activities


import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.apollographql.apollo.ApolloCall
import com.apollographql.apollo.api.Response
import com.apollographql.apollo.exception.ApolloException
import com.plocki.alert.API.modules.FetchCategoriesHandler
import com.plocki.alert.API.modules.UserApi
import com.plocki.alert.MyApplication
import com.plocki.alert.R
import com.plocki.alert.WhoAmIQuery
import com.plocki.alert.fragments.FragmentList
import com.plocki.alert.fragments.FragmentMap
import com.plocki.alert.models.Global
import com.plocki.alert.utils.Store

class Splash : Activity() {

    /** Duration of wait  */
    private val SPLASH_DISPLAY_LENGTH = 100
    private val  PERMISSION_LOCATION = 101

    /** Called when the activity is first created.  */
    public override fun onCreate(icicle: Bundle?) {
        super.onCreate(icicle)
        setContentView(R.layout.splash)

        val store = Store(this)
        try {
            Global.getInstance()!!.currentDistanceFilter = store.retrieveDistance()!!
            Global.getInstance()!!.userToken = store.retrieveToken()
        }catch (ex : Exception){
                ex.printStackTrace()
        }

        if(Global.getInstance()!!.currentDistanceFilter == ""){
            Global.getInstance()!!.currentDistanceFilter = "Nielimitowane"
        }

        Handler().postDelayed({
            if (getPermissionsLocation()){
                launchApp()
            }
        }, SPLASH_DISPLAY_LENGTH.toLong())
    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when(requestCode){
            PERMISSION_LOCATION -> {
                Toast.makeText(this, "Permission granted", Toast.LENGTH_SHORT).show()
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    launchApp()
                }

                else{
                    finish()
                }
            }
        }
    }


    private fun launchApp(){
        if(Global.getInstance()!!.userToken != "") {
            UserApi.whoAmI(object : ApolloCall.Callback<WhoAmIQuery.Data>(){
                override fun onResponse(response: Response<WhoAmIQuery.Data>) {
                    val whoAmI = response.data()!!.whoAmI()
                    Global.getInstance()!!.userName = whoAmI.username()
                    Global.getInstance()!!.isUserSigned = true

                    val intent = Intent(MyApplication.context, MainActivity::class.java)
                    intent.putExtra("SHOW_WELCOME", true)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    MyApplication.context!!.startActivity(intent)


                }

                override fun onFailure(e: ApolloException) {
                    Global.getInstance()!!.userToken = ""
                    val sharedstore = Store(this@Splash)
                    sharedstore.removeToken()
                    val intent = Intent(MyApplication.context, LoginPanel::class.java)
                    intent.putExtra("SHOW_WELCOME", true)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    MyApplication.context!!.startActivity(intent)

                }

            })

        }
        else{
            val mainIntent = Intent(this@Splash, LoginPanel::class.java)
            this@Splash.startActivity(mainIntent)
            this@Splash.finish()
            finish()
        }
    }

    private fun getPermissionsLocation(): Boolean {
        return if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_DENIED){
            val permissions = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION)
            ActivityCompat.requestPermissions(this, permissions, PERMISSION_LOCATION)
            false
        } else{
            true
        }
    }


}