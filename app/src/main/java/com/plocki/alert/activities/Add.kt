package com.plocki.alert.activities

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.ContentValues
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.PopupMenu
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.apollographql.apollo.ApolloCall
import com.apollographql.apollo.api.Response
import com.apollographql.apollo.exception.ApolloException
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.plocki.alert.CreateEventMutation
import com.plocki.alert.R
import com.plocki.alert.models.Event
import kotlinx.android.synthetic.main.activity_add.*
import com.plocki.alert.models.EventMethods.Companion.thumbnailFromUri
import com.plocki.alert.models.Global
import com.plocki.alert.utils.FileGetter
import com.plocki.alert.utils.MyApolloClient
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.ByteArrayInputStream
import java.io.File
import java.util.*
import kotlin.properties.Delegates


class Add : AppCompatActivity(), OnMapReadyCallback {


    val inst = Global.getInstance()

    companion object {
        var hasLocation = false
        var choose = 0
        var lat = 0.0
        var long = 0.0
        lateinit var mMap :GoogleMap
        lateinit var lastLocation: Location
        lateinit var image_uri : Uri
        const val PICK_CODE = 101
        private const val IMAGE_PICK_CODE = 2000
        private const val CAMERA_CODE = 2001
        private const val PERMISSION_READ = 1001
        private const val PERMISSION_CAMERA= 1002
    }

    //OVERRIDES
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add)

        supportActionBar!!.title = this.getString(R.string.add_menu_title)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.fragmini) as SupportMapFragment
        mapFragment.getMapAsync(this)

        add_title.setOnFocusChangeListener { _, b ->
            if(!b){
                validateTitle()
            }

        }

        category_in.keyListener = null
        category.setOnClickListener{ onChooseCategoryClick() }
        category_in.setOnClickListener{ onChooseCategoryClick() }

        imageclick.setOnClickListener{ onAddImageClick() }
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.add_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId

        if (id == R.id.action_done) {
            addEvent()

        }
        if (id == android.R.id.home) {
            finish()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when(requestCode){
            PERMISSION_READ -> {
                if (grantResults.isNotEmpty() && grantResults[0] ==
                    PackageManager.PERMISSION_GRANTED){
                    pickImageFromGallery()
                }

                else{
                    Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
                }
            }
            PERMISSION_CAMERA -> {
                if (grantResults.isNotEmpty() && grantResults[0] ==
                    PackageManager.PERMISSION_GRANTED){
                    launchCamera()
                }

                else{
                    Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK && requestCode == IMAGE_PICK_CODE){
            val uri = data?.data

            if (uri != null) {
                image_uri = data?.data
            }

//            val apolloClient = MyApolloClient()
//            apolloClient.createEvent(File(res))
            image.background = thumbnailFromUri(this, uri)
            add_photo_text.text = ""
            imageButton.visibility = View.INVISIBLE
        }
        if (resultCode == Activity.RESULT_OK && requestCode == CAMERA_CODE){
            image.background = thumbnailFromUri(this, image_uri)

            add_photo_text.text = ""
            imageButton.visibility = View.INVISIBLE
        }
        if (resultCode == Activity.RESULT_OK && requestCode == PICK_CODE){
            hasLocation = true
            val coords = data?.data
            val tab = coords.toString().split('+')
            lat = tab[0].toDouble()
            long = tab[1].toDouble()

            mMap.clear()
            mMap.addMarker(
                MarkerOptions()
                    .position(LatLng(lat, long))
                    .title(this.getString(R.string.add_location_pin_text))
            )
        }

        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(LatLng(lat,long), 14f),1, null)
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onMapReady(googleMap: GoogleMap) {

        mMap = googleMap
        mMap.mapType = GoogleMap.MAP_TYPE_NORMAL

        mMap.clear() //clear old markers

        mMap.uiSettings.isScrollGesturesEnabled = false
        mMap.uiSettings.isCompassEnabled = false
        mMap.uiSettings.isMyLocationButtonEnabled = false
        mMap.uiSettings.isZoomGesturesEnabled = false

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED) {
            mMap.isMyLocationEnabled = true
        } else {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
                mMap.isMyLocationEnabled = true

            }
        }


        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        fusedLocationClient.lastLocation.addOnSuccessListener {
            if (it != null) {
                lastLocation = it
                val currentLatLng = LatLng(it.latitude, it.longitude)
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 14f),1, null)
            }
        }

        mMap.setOnMapClickListener {
            val intent = Intent(this@Add, LocationPicker::class.java)
            var locationTransformed = ""
            if(hasLocation){
                locationTransformed = "$lat+$long"
            }
            intent.putExtra("Coords", locationTransformed)
            startActivityForResult(intent, PICK_CODE)
        }

    }

    //ON CLICKS
    fun onAddImageClick() {
        val menuItemView = findViewById<View>(R.id.image) // SAME ID AS MENU ID
        val context = applicationContext
        val popupMenu = PopupMenu(context, menuItemView)
        popupMenu.inflate(R.menu.image_menu)

        popupMenu.setOnMenuItemClickListener {
            val id = it.itemId
            if (id == R.id.action_choose_photo){
                permissionsRead()

            }
            if (id == R.id.action_take_photo){
                permissionsCamera()
            }
            return@setOnMenuItemClickListener true
        }

        popupMenu.show()
    }


    fun onChooseCategoryClick() {
        val singleChoiceItems = inst!!.CategoryList

        val itemSelected = choose
        val tmp = AlertDialog.Builder(this, R.style.CustomDialogTheme)
            .setTitle(this.getString(R.string.add_dialog_title))
            .setSingleChoiceItems(singleChoiceItems, itemSelected) {
                    dialogInterface, selectedIndex -> choose = selectedIndex}
            .setPositiveButton(this.getString(R.string.add_dialog_positive)) {
                    dialog, which ->
                         category_in.setText(singleChoiceItems[choose])  }
            .setNegativeButton(this.getString(R.string.add_dialog_negative), null)
            .show()


       val but = tmp.getButton(DialogInterface.BUTTON_POSITIVE)
        but.setTextColor(Color.parseColor("#6200EE"))
        val but2 = tmp.getButton(DialogInterface.BUTTON_NEGATIVE)
        but2.setTextColor(Color.parseColor("#6200EE"))
    }

    //PRIVATES
    private fun launchCamera() {
        val values = ContentValues()
        values.put(MediaStore.Images.Media.TITLE, "New Picture")
        values.put(MediaStore.Images.Media.DESCRIPTION, "From the Camera")
        image_uri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
        //camera intent
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, image_uri)
        startActivityForResult(cameraIntent, CAMERA_CODE)
    }

    private fun pickImageFromGallery() {
        //Intent to pick image
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, IMAGE_PICK_CODE)
    }

    private fun permissionsRead(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED){
                val permissions = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
                ActivityCompat.requestPermissions(this, permissions, PERMISSION_READ)
            }
            else{
                pickImageFromGallery()
            }
        }
        else{
            pickImageFromGallery()
        }
    }

    private fun permissionsCamera(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED){

                val permissions = arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA)
                ActivityCompat.requestPermissions(this, permissions, PERMISSION_CAMERA)
            }
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED){

                val permissions = arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                ActivityCompat.requestPermissions(this, permissions, PERMISSION_CAMERA)
            }
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED){
                val permissions = arrayOf( Manifest.permission.CAMERA)
                ActivityCompat.requestPermissions(this, permissions, PERMISSION_CAMERA)
            }
            else{
                launchCamera()
            }
        }
        else{
            launchCamera()
        }




    }

    private fun validateTitle(): Boolean {
        val text = add_title.text.toString()
        return if(text.isEmpty()){
            lay_title.error = getString(R.string.add_title_error)
            false
        } else{
            lay_title.error = null
            true
        }
    }

    fun addEvent() {
        val path = FileGetter.getRealPath(image_uri, contentResolver)
        val event = Event(
            Global.getInstance()!!.list.size,
            UUID.randomUUID(),
            LatLng(lat, long),
            path,
            add_title.text.toString(),
            desc2.text.toString(),
            1,
            1
        )
        val apolloClient = MyApolloClient()
        progressBar.visibility = View.VISIBLE
        GlobalScope.launch {val createEventResult = apolloClient.createEvent(event)  }
        GlobalScope.launch {

            delay(2000)
            finish()
        }
    }




}


