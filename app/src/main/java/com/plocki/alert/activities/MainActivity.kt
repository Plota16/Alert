package com.plocki.alert.activities

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.PopupMenu
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.plocki.alert.API.modules.FetchEventsHandler
import com.plocki.alert.R
import com.plocki.alert.fragments.FragmentList
import com.plocki.alert.fragments.FragmentMap
import com.plocki.alert.fragments.FragmentProfile
import com.plocki.alert.models.Global
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    companion object {

        private const val  PERMISSION_LOCATION = 101
    }

    private val inst = Global.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportActionBar!!.title = "Alert!"
        supportActionBar!!.setIcon(ResourcesCompat.getDrawable(resources, R.drawable.ic_more, null))


        getPermissionsLocation()

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

        var currentFragment = "Map"
        val manager = supportFragmentManager
        var transaction = manager.beginTransaction()
        val textFragmentMap = FragmentMap()
        val textFragmentList = FragmentList()
        val textFragmentProfile = FragmentProfile()


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

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when(requestCode){
            PERMISSION_LOCATION -> {
                Toast.makeText(this, "Permission granted", Toast.LENGTH_SHORT).show()
                if (grantResults.isNotEmpty() && grantResults[0] ==
                    PackageManager.PERMISSION_GRANTED){
                    val textFragmentA = FragmentMap()
                    val textFragmentB = FragmentList()
                    val manager = supportFragmentManager
                    var transaction = manager.beginTransaction()
                    transaction = manager.beginTransaction()
                    transaction.hide(textFragmentB)
                    transaction.show(textFragmentA)
                    transaction.commit()
                }

                else{
                    Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun getPermissionsLocation(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_DENIED){
            val permissions = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION)
                ActivityCompat.requestPermissions(this, permissions, PERMISSION_LOCATION)
        }
    }

}
