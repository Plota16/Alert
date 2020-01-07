package com.plocki.alert.activities

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.*
import android.widget.Button
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.FragmentTransaction
import com.apollographql.apollo.ApolloCall
import com.apollographql.apollo.api.Response
import com.apollographql.apollo.exception.ApolloException
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.gson.GsonBuilder
import com.plocki.alert.API.ApolloInstance
import com.plocki.alert.API.modules.EventsApi
import com.plocki.alert.API.modules.FetchEventsHandler
import com.plocki.alert.AllEventsQuery
import com.plocki.alert.R
import com.plocki.alert.fragments.FragmentList
import com.plocki.alert.fragments.FragmentMap
import com.plocki.alert.fragments.FragmentProfile
import com.plocki.alert.models.*
import com.plocki.alert.utils.HttpErrorHandler
import kotlinx.android.synthetic.main.activity_add.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.dialog_report.*
import kotlinx.android.synthetic.main.dialog_report.desc2
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private val inst = Global.getInstance()
    private var currentFragment = "Map"
    private val textFragmentMap = FragmentMap()
    private val textFragmentList = FragmentList()
    private val textFragmentProfile = FragmentProfile()
    private var isBlocked = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportActionBar!!.title = "Alert!"
        supportActionBar!!.setIcon(ResourcesCompat.getDrawable(resources, R.drawable.ic_more, null))
        Global.getInstance()!!.currentActivity = this

        addbutton.setOnClickListener {
            val intent = Intent(this@MainActivity, Add::class.java)
            intent.putExtra("SHOW_WELCOME", true)
            startActivity(intent)
        }
        filterbutton.setOnClickListener{
            val intent = Intent(this@MainActivity, Filter::class.java)
            intent.putExtra("SHOW_WELCOME", true)
            startActivity(intent)
        }

        val manager = supportFragmentManager
        var transaction = manager.beginTransaction()

        transaction.add(R.id.details_fragment,textFragmentMap)
        transaction.add(R.id.details_fragment,textFragmentProfile)
        transaction.hide(textFragmentProfile)
        transaction.commit()

        bottom_navigation.setOnNavigationItemSelectedListener {
            when (it.itemId){
                R.id.action_map -> {
                    addbutton.show()
                    filterbutton.show()
                    transaction = manager.beginTransaction()

                    when(currentFragment){
                        "List" -> {
                            transaction.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right)
                            transaction.hide(textFragmentList)
                        }
                        "Profile" -> {
                            transaction.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right)
                            transaction.hide(textFragmentProfile)
                        }
                    }

                    transaction.show(textFragmentMap)
                    currentFragment = "Map"
                    transaction.commit()
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.action_list -> {
                    addbutton.show()
                    filterbutton.show()
                    transaction = manager.beginTransaction()
                    if(inst!!.isMapCreated)
                    {
                        transaction.add(R.id.details_fragment,textFragmentList)
                        inst.isMapCreated = false
                    }
                    when(currentFragment){
                        "Profile" -> {
                            transaction.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right)
                            transaction.hide(textFragmentProfile)
                        }
                        "Map" -> {
                            transaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left)
                            transaction.hide(textFragmentMap)
                        }
                    }
                    transaction.show(textFragmentList)
                    currentFragment = "List"
                    transaction.commit()
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.action_profile -> {
                    addbutton.hide()
                    filterbutton.hide()
                    transaction = manager.beginTransaction()
                    when(currentFragment){
                        "List" -> {
                            transaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left)
                            transaction.hide(textFragmentList)
                        }
                        "Map" -> {
                            transaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left)
                            transaction.hide(textFragmentMap)
                        }
                    }

                    transaction.show(textFragmentProfile)
                    currentFragment = "Profile"
                    transaction.commit()
                    return@setOnNavigationItemSelectedListener true
                }
                else ->{
                    transaction = manager.beginTransaction()
                    transaction.hide(textFragmentList)
                    transaction.show(textFragmentMap)
                    transaction.commit()
                    return@setOnNavigationItemSelectedListener true
                }
            }
        }

    }

    override fun onBackPressed() {
        if(!isBlocked){
            if(currentFragment == "Map"){
                finishAffinity()
            }
            else{
                bottom_navigation.selectedItemId = R.id.action_map
                val manager = supportFragmentManager
                val transaction: FragmentTransaction
                addbutton.show()
                filterbutton.show()
                transaction = manager.beginTransaction()

                when(currentFragment){
                    "List" -> {
                        transaction.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right)
                        transaction.hide(textFragmentList)
                    }
                    "Profile" -> {
                        transaction.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right)
                        transaction.hide(textFragmentProfile)
                    }
                }

                transaction.show(textFragmentMap)
                currentFragment = "Map"
                transaction.commit()
            }
        }

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.top_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        val id = item.itemId

        if (id == R.id.action_refresh) {
            GlobalScope.launch(Main){
                block()
                fetchEvents()
            }
        }

        if (id == R.id.action_more) {
            val menuItemView = findViewById<View>(R.id.action_more) // SAME ID AS MENU ID
            val popupMenu = PopupMenu(this, menuItemView)
            popupMenu.inflate(R.menu.more_menu)
            popupMenu.setOnMenuItemClickListener {
                val moreMenuId = it.itemId
                if (moreMenuId == R.id.action_info){
                    Toast.makeText(this@MainActivity, "Info", Toast.LENGTH_LONG).show()
                }
                if (moreMenuId == R.id.action_settings){
                    Toast.makeText(this@MainActivity, "Ustawienia", Toast.LENGTH_LONG).show()
                }
                return@setOnMenuItemClickListener true
            }
            popupMenu.show()
            // ...
            return true


        }
        return super.onOptionsItemSelected(item)
    }

    private fun block(){
        isBlocked = true
        progressBarMap.visibility = View.VISIBLE
        this.window.setFlags(
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
    }

    private fun unBlock(){
        isBlocked = false
        progressBarMap.visibility = View.GONE
        this.window.clearFlags(
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
    }

    private fun fetchEvents(){
        if(!Global.getInstance()!!.isErrorActivityOpen && Global.getInstance()!!.isUserSigned){

            ApolloInstance.buildApolloClient()
            EventsApi.fetchEvents(object : ApolloCall.Callback<AllEventsQuery.Data>() {
                override fun onFailure(e: ApolloException) {
                    //TODO sprawdzić czy zakomentować linię niżej
                    this@MainActivity.runOnUiThread { Toast.makeText(this@MainActivity, "Nie udało się pobrać danych z serwera", Toast.LENGTH_SHORT).show() }
                    HttpErrorHandler.handle(500)
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
                                1,
                                event.totalLikes(),
                                LikeType.getNameByType(event.userLike())
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
                        GlobalScope.launch(Main){
                            unBlock()
                        }


                    } else {
                        this@MainActivity.runOnUiThread { Toast.makeText(this@MainActivity, "Nie udało się pobrać danych z serwera", Toast.LENGTH_SHORT).show()}
                    }



                }

            })
        }
    }



}
