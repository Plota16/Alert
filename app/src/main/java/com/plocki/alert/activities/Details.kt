package com.plocki.alert.activities

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.plocki.alert.models.Event
import com.plocki.alert.models.Global
import com.plocki.alert.R
import kotlinx.android.synthetic.main.activity_details.*

class Details : AppCompatActivity(), OnMapReadyCallback {


    lateinit var mMap : GoogleMap
    lateinit var lastLocation: Location
    private lateinit var event : Event
    var inst = Global.getInstance()
    var INTERNET = 101


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_details)

        supportActionBar!!.title = "Dodawanie Wydarzenia"
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        var bundle :Bundle ?=intent.extras

        var listMarker: String? = bundle!!.getString("pos")
        var extraMarker : String? = bundle!!.getString("Marker")


        if(extraMarker == null){
            this.event = inst!!.listHashMap[listMarker]!!
        }
        else if(listMarker == null){
            this.event = inst!!.mapHashMap[extraMarker]!!
        }

        Glide.with(this).load(event.image).into(details_image)
        details_id.text = event!!.title
        details_category.text = event!!.category
        details_desc.text = event!!.desctription

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

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED) {
            mMap.isMyLocationEnabled = true
        }
        else {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
                mMap.isMyLocationEnabled = true

            } else {

            }
        }
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(event.coords, 16f),1, null)
        fusedLocationClient.lastLocation.addOnSuccessListener {
            if (it != null) {
                Add.lastLocation = it
                val currentLatLng = LatLng(it.latitude, it.longitude)

            }
        }

        mMap.setOnMapClickListener {
            val intent = Intent(this@Details, LocationPicker::class.java)
            var tmp = ""
            if(Add.hasLocation){
                tmp = "${Add.lat}+${Add.long}"
            }
            intent.putExtra("Coords", tmp)
            startActivityForResult(intent, Add.PICK_CODE)
        }
        mMap.addMarker(
            MarkerOptions()
                .position(event.coords)
                .title(event.title))
    }
}

