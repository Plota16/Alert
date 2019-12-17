package com.plocki.alert.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.PopupMenu
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import com.apollographql.apollo.ApolloCall
import com.apollographql.apollo.api.Response
import com.apollographql.apollo.exception.ApolloException
import com.plocki.alert.API.ApolloInstance
import com.plocki.alert.API.modules.CategoriesApi
import com.plocki.alert.API.modules.EventsApi
import com.plocki.alert.API.modules.FetchEventsHandler
import com.plocki.alert.AllCategoriesQuery
import com.plocki.alert.AllEventsQuery
import com.plocki.alert.R
import com.plocki.alert.fragments.FragmentList
import com.plocki.alert.fragments.FragmentMap
import com.plocki.alert.fragments.FragmentProfile
import com.plocki.alert.models.Category
import com.plocki.alert.models.Event
import com.plocki.alert.models.Global
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class MainActivity : AppCompatActivity() {

    private val inst = Global.getInstance()
    private var currentFragment = "Map"
    private val textFragmentMap = FragmentMap()
    private val textFragmentList = FragmentList()
    private val textFragmentProfile = FragmentProfile()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportActionBar!!.title = "Alert!"
        supportActionBar!!.setIcon(ResourcesCompat.getDrawable(resources, R.drawable.ic_more, null))

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

        progressBarMain.visibility = View.VISIBLE
        fetchCategories()
    }

    override fun onBackPressed() {
        if(currentFragment == "Map"){
            finishAffinity()
        }
        else{
            bottom_navigation.selectedItemId = R.id.action_map
            val manager = supportFragmentManager
            var transaction = manager.beginTransaction()
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

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.top_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        val id = item.itemId

        if (id == R.id.action_refresh) {
            FetchEventsHandler.fetchEvents(this)
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

    private fun fetchCategories(){
        if (!Global.getInstance()!!.isErrorActivityOpen && Global.getInstance()!!.isUserSigned) {
            ApolloInstance.buildApolloClient()
            CategoriesApi.fetchCategories(object : ApolloCall.Callback<AllCategoriesQuery.Data>() {
                override fun onFailure(e: ApolloException) {
                    println("ERROR FETCH" +  e.cause.toString())
                }

                override fun onResponse(response: Response<AllCategoriesQuery.Data>) {
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
                this@MainActivity.runOnUiThread { Toast.makeText(this@MainActivity, "Nie udało się pobrać danych z serwera", Toast.LENGTH_SHORT).show() }
                Log.e("ERROR FETCH", e.cause.toString())
            }

            override fun onResponse(response: Response<AllEventsQuery.Data>) {

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
                    this@MainActivity?.runOnUiThread { Toast.makeText(this@MainActivity, "Pobrano danych z serwera", Toast.LENGTH_SHORT).show() }

                } else {
                    this@MainActivity?.runOnUiThread { Toast.makeText(this@MainActivity, "Nie udało się pobrać danych z serwera", Toast.LENGTH_SHORT).show()}
                }

                GlobalScope.launch(Main){
                    Global.getInstance()!!.areCategoriesLoaed = true
                    progressBarMain.visibility = View.GONE
                }
            }

        })
    }

}
