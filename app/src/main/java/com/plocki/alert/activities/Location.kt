package com.plocki.alert.activities

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.plocki.alert.R
import com.plocki.alert.models.EventMethods

class Location : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap :GoogleMap
    private lateinit var lastLocation: Location

    var long = 0.0
    var lat = 0.0
    var color = ""
    var title = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_location)

        val bundle :Bundle ?=intent.extras
        val extra: String = bundle!!.getString("cords")!!
        val cords: List<String> = extra.split("~~")
        lat = cords[0].toDouble()
        long = cords[1].toDouble()
        color = cords[2]
        title = cords[3]


        supportActionBar!!.title = title
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)


        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.fragpick) as SupportMapFragment
        mapFragment.getMapAsync(this)

    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        setupMapSettings()
        turnOnMyLocation()
        initMarker()

        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(LatLng(lat,long), 16f), 1 , null)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId

        if (id == android.R.id.home) {
            finish()
        }

        return super.onOptionsItemSelected(item)
    }

    private fun turnOnMyLocation(){
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
    }

    private fun initMarker(){
        mMap.addMarker(
            MarkerOptions()
                .position(LatLng(lat,long))
                .title("Wydarzenie")
                .icon(EventMethods.getMarkerIcon(color)))
    }

    private fun setupMapSettings(){
        mMap.clear()
        mMap.mapType = GoogleMap.MAP_TYPE_NORMAL
        mMap.uiSettings.isScrollGesturesEnabled = false
        mMap.uiSettings.isCompassEnabled = false
        mMap.uiSettings.isMyLocationButtonEnabled = false
        mMap.uiSettings.isZoomGesturesEnabled = false
    }
}
