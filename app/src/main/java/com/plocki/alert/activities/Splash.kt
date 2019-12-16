package com.plocki.alert.activities


import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import com.apollographql.apollo.ApolloCall
import com.apollographql.apollo.api.Response
import com.apollographql.apollo.exception.ApolloException
import com.plocki.alert.API.modules.FetchCategoriesHandler
import com.plocki.alert.API.modules.UserApi
import com.plocki.alert.R
import com.plocki.alert.WhoAmIQuery
import com.plocki.alert.models.Global
import com.plocki.alert.utils.Store

class Splash : Activity() {

    /** Duration of wait  */
    private val SPLASH_DISPLAY_LENGTH = 1000

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

            if(Global.getInstance()!!.userToken != "") {
                UserApi.whoAmI(object : ApolloCall.Callback<WhoAmIQuery.Data>(){
                    override fun onResponse(response: Response<WhoAmIQuery.Data>) {
                        val whoAmI = response.data()!!.whoAmI()
                        Global.getInstance()!!.userName = whoAmI.username()
                        Global.getInstance()!!.isUserSigned = true
                        FetchCategoriesHandler.fetchCategories(this@Splash)

                    }

                    override fun onFailure(e: ApolloException) {
                        Global.getInstance()!!.userToken = ""
                        val sharedstore = Store(this@Splash)
                        sharedstore.removeToken()

                    }

                })

            }
            else{
                val mainIntent = Intent(this@Splash, LoginPanel::class.java)
                this@Splash.startActivity(mainIntent)
                this@Splash.finish()
                finish()
            }
        }, SPLASH_DISPLAY_LENGTH.toLong())
    }

}