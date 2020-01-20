package com.plocki.alert.activities


import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.apollographql.apollo.ApolloCall
import com.apollographql.apollo.api.Response
import com.apollographql.apollo.exception.ApolloException
import com.plocki.alert.*
import com.plocki.alert.api.modules.UserApi
import com.plocki.alert.models.Global
import com.plocki.alert.utils.Store
import kotlinx.android.synthetic.main.splash.*
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import com.google.gson.GsonBuilder
import com.plocki.alert.api.modules.FetchCategoriesHandler.fetchCategories
import com.plocki.alert.utils.HttpErrorHandler

class Splash : Activity() {

    /** Duration of wait  */
    private val splashDisplayLength = 100
    private val  permissionLocation = 101
    lateinit var activity: Activity

    /** Called when the activity is first created.  */
    public override fun onCreate(icicle: Bundle?) {
        super.onCreate(icicle)
        setContentView(R.layout.splash)
        progressBarMain.indeterminateTintList = ColorStateList.valueOf(Color.parseColor("#FFFFFF"))
        Global.getInstance()!!.currentActivity = this
        val store = Store(this)
        try {
            Global.getInstance()!!.userToken = store.retrieveToken()
        }catch (ex : Exception){
                ex.printStackTrace()
        }

        if(Global.getInstance()!!.currentDistanceFilter == ""){
            Global.getInstance()!!.currentDistanceFilter = "Nielimitowane"
        }

        Handler().postDelayed({
            if (getPermissionsLocation()){
                if(!Global.getInstance()!!.isErrorActivityOpen){
                    if(!Global.getInstance()!!.isErrorActivityOpen){
                        launchApp()
                    }
                }
            }
        }, splashDisplayLength.toLong())
    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when(requestCode){
            permissionLocation -> {
                Toast.makeText(this, "Permission granted", Toast.LENGTH_SHORT).show()
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    if(!Global.getInstance()!!.isErrorActivityOpen){
                        launchApp()
                    }
                }

                else{
                    finish()
                }
            }
        }
    }

    override fun onBackPressed() {
    }
    override fun onResume() {
        super.onResume()
        if(!Global.getInstance()!!.isFirstStart){
            if(!Global.getInstance()!!.isErrorActivityOpen){
                launchApp()
            }
        }
        else{
            Global.getInstance()!!.isFirstStart = false
        }
    }

    private fun launchApp(){
        if(Global.getInstance()!!.userToken != "") {
            UserApi.whoAmI(object : ApolloCall.Callback<WhoAmIQuery.Data>(){
                override fun onResponse(response: Response<WhoAmIQuery.Data>) {
                   if (response.hasErrors()) {
                       val gson = GsonBuilder().create()
                       val errorMap = gson.fromJson(response.errors()[0].message(), Map::class.java)
                       HttpErrorHandler.handle(errorMap["statusCode"].toString().toFloat().toInt())
                       return
                   }
                    val whoAmI = response.data()!!.whoAmI()
                    Global.getInstance()!!.userName = whoAmI.username()
                    Global.getInstance()!!.isUserSigned = true
                    println("WHOAMI STATS: " + response.data()!!.myStats().toString())

                    GlobalScope.launch(Main){
                        progressBarMain.visibility = View.VISIBLE
                        splashLoadText.visibility = View.VISIBLE
                    }
                    fetchCategories(this@Splash)
                }

                override fun onFailure(e: ApolloException) {

//                    Global.getInstance()!!.userToken = ""
//                    val sharedstore = Store(this@Splash)
//                    sharedstore.removeToken()
//                    val intent = Intent(MyApplication.context, LoginPanel::class.java)
//                    intent.putExtra("SHOW_WELCOME", true)
//                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
//                    MyApplication.context!!.startActivity(intent)
                    HttpErrorHandler.handle(500)
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
            ActivityCompat.requestPermissions(this, permissions, permissionLocation)
            false
        } else{
            true
        }
    }

}