package com.plocki.alert.activities

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.content.res.ResourcesCompat
import kotlinx.android.synthetic.main.activity_main.*
import android.view.Menu
import android.widget.Toast
import android.view.MenuItem
import android.view.View
import android.widget.PopupMenu
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.plocki.alert.fragments.FragmentList
import com.plocki.alert.R
import com.plocki.alert.fragments.FragmentMap
import kotlinx.android.synthetic.main.map_fragment.*


class MainActivity : AppCompatActivity() {

    companion object {


        private const val  PERMISSION_LOCATION = 101
    }

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

        val manager = supportFragmentManager
        var transaction = manager.beginTransaction()
        val textFragmentA = FragmentMap()
        val textFragmentB = FragmentList()
        transaction.add(R.id.details_fragment,textFragmentA)
        transaction.add(R.id.details_fragment,textFragmentB)
        transaction.hide(textFragmentB)
        transaction.commit()

        bottom_navigation.setOnNavigationItemSelectedListener {
            when (it.itemId){
                R.id.action_map -> {
                    transaction = manager.beginTransaction()
                    transaction.hide(textFragmentB)
                    transaction.show(textFragmentA)
                    transaction.commit()
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.action_list -> {
                    transaction = manager.beginTransaction()
                    transaction.hide(textFragmentA)
                    transaction.show(textFragmentB)
                    transaction.commit()
                    return@setOnNavigationItemSelectedListener true
                }

                else ->{
                    val textFragment = FragmentList()
                    val manager = supportFragmentManager
                    val transaction = manager.beginTransaction()
                    transaction.replace(R.id.details_fragment,textFragment)
                    transaction.addToBackStack(null)
                    transaction.commit()
                    return@setOnNavigationItemSelectedListener true
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.top_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        val id = item.itemId



        if (id == R.id.action_refresh) {
            Toast.makeText(this@MainActivity, "Refresh App", Toast.LENGTH_LONG).show()
        }

        if (id == R.id.action_more) {
            val menuItemView = findViewById<View>(R.id.action_more) // SAME ID AS MENU ID
            val popupMenu = PopupMenu(this, menuItemView)
            popupMenu.inflate(R.menu.more_menu)
            popupMenu.setOnMenuItemClickListener {
                val moreMenuId = it.itemId
                if (moreMenuId == R.id.action_login){
                    val intent = Intent(this@MainActivity, LoginPanel::class.java)
                    startActivity(intent)
                }
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
                    val textFragment = FragmentMap()
                    val manager = supportFragmentManager
                    val transaction = manager.beginTransaction()
                    transaction.replace(R.id.details_fragment,textFragment)
                    transaction.addToBackStack(null)
                    transaction.commit()
                }

                else{
                    Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun getPermissionsLocation(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_DENIED){
                val permissions = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION)
                ActivityCompat.requestPermissions(this, permissions, PERMISSION_LOCATION)
            }
        }
    }

}
