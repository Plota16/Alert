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
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
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
import com.google.gson.GsonBuilder
import com.plocki.alert.API.modules.EventsApi
import com.plocki.alert.AllEventsQuery
import com.plocki.alert.CreateEventMutation
import com.plocki.alert.MyApplication.Companion.context
import com.plocki.alert.R
import com.plocki.alert.models.Event
import kotlinx.android.synthetic.main.activity_add.*
import com.plocki.alert.models.EventMethods.Companion.thumbnailFromUri
import com.plocki.alert.models.Global
import com.plocki.alert.utils.FileGetter
import com.plocki.alert.utils.HttpErrorHandler
import kotlinx.android.synthetic.main.activity_add.progressBar
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.lang.Exception
import java.util.*


class Add : AppCompatActivity(), OnMapReadyCallback {


    private val inst = Global.getInstance()

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
        private const val PERMISSION_STORAGE = 1003
        private const val PERMISSION_STORAGE_AND_CAMERA = 1004
    }

    //OVERRIDES
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add)

        lat = 0.0
        long = 0.0

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
            PERMISSION_STORAGE -> {
                if (grantResults.isNotEmpty() && grantResults[0] ==
                    PackageManager.PERMISSION_GRANTED){
                    launchCamera()
                }
                else{
                    Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
                }
            }
            PERMISSION_STORAGE_AND_CAMERA -> {
                if (grantResults.isNotEmpty() &&
                    grantResults[1] == PackageManager.PERMISSION_GRANTED &&
                    grantResults[1] == PackageManager.PERMISSION_GRANTED){
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
                image_uri = data.data!!
            }

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
            validateLocation()
            mMap.clear()
            mMap.addMarker(
                MarkerOptions()
                    .position(LatLng(lat, long))
                    .title(this.getString(R.string.add_location_pin_text))
            )
        }

        if(hasLocation){
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(LatLng(lat,long), 14f),1, null)
        }
        validateLocation()
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

        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(inst!!.userCameraPosition, 12f), 1, null)

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
            intent.putExtra("coordinate", locationTransformed)
            startActivityForResult(intent, PICK_CODE)
        }

    }

    //ON CLICKS
    private fun onAddImageClick() {
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

    private fun onChooseCategoryClick() {
        val singleChoiceItems :Array<String> = inst!!.categoryList.toTypedArray()

        val itemSelected = choose
        val tmp = AlertDialog.Builder(this, R.style.CustomDialogTheme)
            .setTitle(this.getString(R.string.add_dialog_title))
            .setSingleChoiceItems(singleChoiceItems, itemSelected) {
                    _, selectedIndex ->
                choose = selectedIndex
            }
            .setPositiveButton(this.getString(R.string.add_dialog_positive)) {
                    _, _ ->
                category_in.setText(singleChoiceItems[choose])
                validateCategory() }
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
        image_uri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)!!
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
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED){
            val permissions = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
            ActivityCompat.requestPermissions(this, permissions, PERMISSION_READ)
        }
        else{
            pickImageFromGallery()
        }
    }

    private fun permissionsCamera(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED &&
            ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED){

            val permissions = arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA)
            ActivityCompat.requestPermissions(this, permissions, PERMISSION_STORAGE_AND_CAMERA)
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED){

            val permissions = arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            ActivityCompat.requestPermissions(this, permissions, PERMISSION_STORAGE)
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED){
            val permissions = arrayOf( Manifest.permission.CAMERA)
            ActivityCompat.requestPermissions(this, permissions, PERMISSION_CAMERA)
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

    private fun validateLocation() : Boolean {
        return if(lat != 0.0 && long != 0.0){
            localization_lay.error = null
            localization_textView.setTextColor(ContextCompat.getColor(this,R.color.colorPrimary))
            true
        } else{
            localization_lay.error = getString(R.string.add_location_error)
            localization_textView.setTextColor(ContextCompat.getColor(this,R.color.errorRed))
            false
        }
    }

    private fun validateCategory(): Boolean {
        val temp = category_in.text.toString()
        return if(temp != ""){
            category.error = null
            true
        } else{
            category.error = getString(R.string.add_category_error)
            false
        }
    }

    private fun addEvent() {

        val titleValidation = validateTitle()
        val categoryValidation = validateCategory()
        val locationValidation = validateLocation()

        if(titleValidation && categoryValidation && locationValidation){
            var path = ""
            try{
                if (image_uri != Uri.EMPTY) {
                    path = FileGetter.getRealPath(image_uri, contentResolver)
                }
            }catch (ex : Exception){}

            val tempCategory = category_in.text.toString()
            val event = Event(
                UUID = UUID.randomUUID(),
                coordinates = LatLng(lat, long),
                image = path,
                title = add_title.text.toString(),
                description = desc2.text.toString(),
                category = Global.getInstance()!!.categoryHashMap[Global.getInstance()!!.titleUUIDHashMap[tempCategory]]!!,
                creator = 1
            )
            window.setFlags(
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
            progressBar.visibility = View.VISIBLE
            val createEventDto = event.createEventDto(this)
            GlobalScope.launch {
                EventsApi.createEvent(
                    createEventDto,
                    object : ApolloCall.Callback<CreateEventMutation.Data>() {
                        override fun onFailure(e: ApolloException) {
                            val gson = GsonBuilder().create()
                            val errorMap = gson.fromJson(e.message, Map::class.java)
                            HttpErrorHandler.handle(errorMap["statusCode"].toString().toFloat().toInt())
                        }

                        override fun onResponse(response: Response<CreateEventMutation.Data>) {
                            Log.d("SUCCESS", response.data().toString())
                            if (response.hasErrors()) {
                                Log.e("ERROR ", response.errors()[0].customAttributes()["statusCode"].toString())
                                val gson = GsonBuilder().create()
                                val errorMap = gson.fromJson(response.errors()[0].message(), Map::class.java)
                                HttpErrorHandler.handle(errorMap["statusCode"].toString().toFloat().toInt())
                                return
                            }
                            EventsApi.fetchEvents(object : ApolloCall.Callback<AllEventsQuery.Data>() {
                                override fun onFailure(e: ApolloException) {
                                    val gson = GsonBuilder().create()
                                    val errorMap = gson.fromJson(e.message, Map::class.java)
                                    HttpErrorHandler.handle(errorMap["statusCode"].toString().toFloat().toInt())
                                    progressBar.visibility = View.INVISIBLE
                                }

                                override fun onResponse(response: Response<AllEventsQuery.Data>) {
                                    if (response.hasErrors()) {
                                        val gson = GsonBuilder().create()
                                        val errorMap = gson.fromJson(response.errors()[0].message(), Map::class.java)
                                        HttpErrorHandler.handle(errorMap["statusCode"].toString().toFloat().toInt())
                                        return
                                    }
                                    val events = response.data()!!.events()
                                    Log.d(
                                        "AA",
                                        "RESPONSE" + response.data()!!.events()
                                    )
                                    val eventContainer = ArrayList<Event>()
                                    for (singleEvent in events) {
                                        val currentEvent = Event.fromResponse(
                                            singleEvent.uuid().toString(),
                                            singleEvent.coords(),
                                            singleEvent.title(),
                                            singleEvent.image(),
                                            "opis",
                                            Global.getInstance()!!.categoryHashMap[Global.getInstance()!!.titleUUIDHashMap[tempCategory]]!!,
                                            1
                                        )
                                        eventContainer.add(currentEvent)
                                    }
                                    Global.getInstance()!!.eventList = eventContainer
                                    progressBar.visibility = View.INVISIBLE
                                    finish()
                                }
                            })
                        }
                    }
                )
            }
        }

    }
}


