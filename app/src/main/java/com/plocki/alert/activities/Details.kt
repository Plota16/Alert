package com.plocki.alert.activities

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.MenuItem
import android.view.View
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.MarkerOptions
import com.plocki.alert.models.Event
import com.plocki.alert.models.Global
import com.plocki.alert.R
import com.plocki.alert.models.EventMethods
import kotlinx.android.synthetic.main.activity_details.*
import kotlinx.android.synthetic.main.likebar.*
import kotlin.math.roundToInt


class Details : AppCompatActivity(), OnMapReadyCallback {

    var like = false
    var dislike = false

    lateinit var mMap : GoogleMap
    private lateinit var event : Event
    var inst = Global.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_details)

        val bundle :Bundle ?=intent.extras

        val listMarker: String? = bundle!!.getString("pos")
        val extraMarker : String? = bundle.getString("Marker")


        if(extraMarker == null){
            this.event = inst!!.listHashMap[listMarker]!!
        }
        else if(listMarker == null){
            this.event = inst!!.mapHashMap[extraMarker]!!
        }
        details_category.text = EventMethods.getCategory(event.category)
        details_desc.text = event.desctription
        supportActionBar!!.title = event.title
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        val display = windowManager.defaultDisplay
        val outMetrics = DisplayMetrics()
        display.getMetrics(outMetrics)

        var dpWidth = outMetrics.widthPixels
        val dpHeight = dpWidth.toDouble()/4*3


        details_image.layoutParams.width = dpWidth
        details_image.layoutParams.height = dpHeight.roundToInt()
        details_image.requestLayout()

        Glide.with(this)
            .load("http:/192.168.1.56:3000/static/${event.image}.jpg")
            .placeholder(R.drawable.placeholder)
            .centerCrop()
            .override(dpWidth ,dpHeight.roundToInt())
            .into(details_image)

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.details_map) as SupportMapFragment
        mapFragment.getMapAsync(this)

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId

        if (id == android.R.id.home) {
            finish()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onMapReady(p0: GoogleMap?) {
        mMap = p0!!
        mMap.mapType = GoogleMap.MAP_TYPE_NORMAL

        mMap.clear() //clear old markers

        mMap.uiSettings.isScrollGesturesEnabled = false
        mMap.uiSettings.isCompassEnabled = false
        mMap.uiSettings.isMyLocationButtonEnabled = false
        mMap.uiSettings.isZoomGesturesEnabled = false
        mMap.uiSettings.isZoomControlsEnabled = true

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED) {
            mMap.isMyLocationEnabled = true
        }
        else {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
                mMap.isMyLocationEnabled = true

            }
        }
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(event.coords, 15f),1, null)
        fusedLocationClient.lastLocation.addOnSuccessListener {
            if (it != null) {
                Add.lastLocation = it
            }
        }


        mMap.addMarker(
            MarkerOptions()
                .position(event.coords)
                .title(event.title))
    }

    fun likeClicked(v: View){
        if(!like && !dislike){
            like = true
        }
        else if(like && !dislike){
            like = false
        }
        else if(!like && dislike){
            dislike = false
            like = true
        }
        doColor()
    }

    fun dislikeClicked(v: View){
        if(!like && !dislike){
            dislike = true
        }
        else if(like && !dislike){
            like = false
            dislike = true
        }
        else if(!like && dislike){
            dislike = false
        }
        doColor()
    }

    fun doColor(){
        if(!like && !dislike){
            thumb_down.foreground.alpha = 255
            thump_up.foreground.alpha = 255
        }
        else if(like && !dislike){
            thump_up.foreground.alpha = 255
            thumb_down.foreground.alpha = 128
        }
        else if(!like && dislike){
            thump_up.foreground.alpha = 128
            thumb_down.foreground.alpha = 255
        }
    }
}

