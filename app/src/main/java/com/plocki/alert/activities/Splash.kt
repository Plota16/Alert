package com.plocki.alert.activities


import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.apollographql.apollo.ApolloCall
import com.apollographql.apollo.api.Response
import com.apollographql.apollo.exception.ApolloException
import com.plocki.alert.*
import com.plocki.alert.API.ApolloInstance
import com.plocki.alert.API.modules.CategoriesApi
import com.plocki.alert.API.modules.EventsApi
import com.plocki.alert.API.modules.FetchCategoriesHandler
import com.plocki.alert.API.modules.UserApi
import com.plocki.alert.fragments.FragmentList
import com.plocki.alert.fragments.FragmentMap
import com.plocki.alert.models.Category
import com.plocki.alert.models.Event
import com.plocki.alert.models.Global
import com.plocki.alert.utils.Store
import kotlinx.android.synthetic.main.splash.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import com.google.gson.GsonBuilder
import com.plocki.alert.utils.HttpErrorHandler
import kotlin.math.roundToInt

class Splash : Activity() {

    /** Duration of wait  */
    private val SPLASH_DISPLAY_LENGTH = 100
    private val  PERMISSION_LOCATION = 101
    lateinit var activity: Activity

    /** Called when the activity is first created.  */
    public override fun onCreate(icicle: Bundle?) {
        super.onCreate(icicle)
        setContentView(R.layout.splash)
        progressBarMain.indeterminateTintList = ColorStateList.valueOf(Color.parseColor("#FFFFFF"))
        Global.getInstance()!!.currentActivity = this
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
                   if (response.hasErrors()) {
                       val gson = GsonBuilder().create()
                       val errorMap = gson.fromJson(response.errors()[0].message(), Map::class.java)
                       HttpErrorHandler.handle(errorMap["statusCode"].toString().toFloat().toInt())
                       return
                   }
                    val whoAmI = response.data()!!.whoAmI()
                    Global.getInstance()!!.userName = whoAmI.username()
                    Global.getInstance()!!.isUserSigned = true


                    GlobalScope.launch(Main){
                        progressBarMain.visibility = View.VISIBLE
                        splashLoadText.visibility = View.VISIBLE
                    }
                    fetchCategories()
                }

                override fun onFailure(e: ApolloException) {

//                    Global.getInstance()!!.userToken = ""
//                    val sharedstore = Store(this@Splash)
//                    sharedstore.removeToken()
//                    val intent = Intent(MyApplication.context, LoginPanel::class.java)
//                    intent.putExtra("SHOW_WELCOME", true)
//                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
//                    MyApplication.context!!.startActivity(intent)
                    val gson = GsonBuilder().create()
                    val errorMap = gson.fromJson(e.message, Map::class.java)
                    HttpErrorHandler.handle(errorMap["statusCode"].toString().toFloat().toInt())
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

    private fun fetchCategories(){
        if (!Global.getInstance()!!.isErrorActivityOpen && Global.getInstance()!!.isUserSigned) {
            ApolloInstance.buildApolloClient()
            CategoriesApi.fetchCategories(object : ApolloCall.Callback<AllCategoriesQuery.Data>() {
                override fun onFailure(e: ApolloException) {
                    val gson = GsonBuilder().create()
                    val errorMap = gson.fromJson(e.message, Map::class.java)
                    HttpErrorHandler.handle(errorMap["statusCode"].toString().toFloat().toInt())
                }

                override fun onResponse(response: Response<AllCategoriesQuery.Data>) {
                    if (response.hasErrors()) {
                        Log.e("ERROR ", response.errors()[0].customAttributes()["statusCode"].toString())
                        val gson = GsonBuilder().create()
                        val errorMap = gson.fromJson(response.errors()[0].message(), Map::class.java)
                        HttpErrorHandler.handle(errorMap["statusCode"].toString().toFloat().toInt())
                        return
                    }

                    if (response.data() != null) {
                        println("CATEGORIES " + response.data()!!.categories().toString())
                        for (category in response.data()!!.categories()){
                            Global.getInstance()!!.categoryHashMap[category.uuid().toString()] =
                                Category(
                                    category.uuid().toString(),
                                    category.title(),
                                    category.color()
                                )
                            Global.getInstance()!!.categoryList.add(category.title())
                            Global.getInstance()!!.filterHashMap[category.title()] = true
                            Global.getInstance()!!.titleUUIDHashMap[category.title()] = category.uuid().toString()
                        }
                        fetchEvents()
                    }
                }
            })
        }
    }

    private fun fetchEvents(){

        ApolloInstance.buildApolloClient()
        EventsApi.fetchEvents(object : ApolloCall.Callback<AllEventsQuery.Data>() {
            override fun onFailure(e: ApolloException) {
                //TODO sprawdzić czy usunąć linię poniżej
                this@Splash.runOnUiThread { Toast.makeText(this@Splash, "Nie udało się pobrać danych z serwera", Toast.LENGTH_SHORT).show() }
                val gson = GsonBuilder().create()
                val errorMap = gson.fromJson(e.message, Map::class.java)
                HttpErrorHandler.handle(errorMap["statusCode"].toString().toFloat().toInt())
            }

            override fun onResponse(response: Response<AllEventsQuery.Data>) {
                if (response.hasErrors()) {
                    Log.e("ERROR ", response.errors()[0].customAttributes()["statusCode"].toString())
                    val gson = GsonBuilder().create()
                    val errorMap = gson.fromJson(response.errors()[0].message(), Map::class.java)
                    HttpErrorHandler.handle(errorMap["statusCode"].toString().toFloat().toInt())
                    return
                }

                if (response.data() != null) {
                    val events = response.data()!!.events()
                    val eventContainer = ArrayList<Event>()
                    for (event in events) {
                        val currentEvent = Event.fromResponse(
                            event.uuid().toString(),
                            event.coords(),
                            event.title(),
                            event.image(),
                            event.description(),
                            Category(
                                event.category()!!.uuid().toString(),
                                event.category()!!.title(),
                                event.category()!!.color()),
                            1
                        )
                        eventContainer.add(currentEvent)
                    }

                    if (Global.getInstance()!!.eventList.size != eventContainer.size) {
                        if(Global.getInstance()!!.isDataLoadedFirstTime){
                            Global.getInstance()!!.isDataLoadedFirstTime = false
                        }
                        else{
                            Global.getInstance()!!.isDataChanged = true
                        }
                    } else {
                        for (i in 0 until Integer.max(
                            Global.getInstance()!!.eventList.size,
                            eventContainer.size
                        )) {
                            val event1 = Global.getInstance()!!.eventList[i].UUID
                            val event2 = eventContainer[i].UUID
                            if (event1 != event2) {
                                if(Global.getInstance()!!.isDataLoadedFirstTime){
                                    Global.getInstance()!!.isDataLoadedFirstTime = false
                                }
                                else{
                                    Global.getInstance()!!.isDataChanged = true
                                }
                            }
                        }

                    }

                    Global.getInstance()!!.eventList = eventContainer
                    this@Splash?.runOnUiThread { Toast.makeText(this@Splash, "Pobrano danych z serwera", Toast.LENGTH_SHORT).show() }

                } else {
                    this@Splash?.runOnUiThread { Toast.makeText(this@Splash, "Nie udało się pobrać danych z serwera", Toast.LENGTH_SHORT).show()}
                }

                GlobalScope.launch(Dispatchers.Main){
                    Global.getInstance()!!.areCategoriesLoaed = true

                    val intent = Intent(MyApplication.context, MainActivity::class.java)
                    intent.putExtra("SHOW_WELCOME", true)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    MyApplication.context!!.startActivity(intent)
                }
            }

        })
    }

}