package com.plocki.alert.activities

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.plocki.alert.R
import com.plocki.alert.models.EventMethods
import kotlinx.android.synthetic.main.activity_location_picker.*

class Location : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap :GoogleMap
    private lateinit var lastLocation: Location

    var long = 0.0
    var lat = 0.0
    var color = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_location)
        val bundle :Bundle ?=intent.extras
        val extra = bundle!!.getString("cords")
        val coords = extra.split("~~")
        lat = coords[0].toDouble()
        long = coords[1].toDouble()
        color = coords[2]
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.fragpick) as SupportMapFragment
        mapFragment.getMapAsync(this)

    }

    override fun onMapReady(googleMap: GoogleMap) {


        mMap = googleMap
        mMap.mapType = GoogleMap.MAP_TYPE_NORMAL

        mMap.clear()

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
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(LatLng(lat,long), 16f), 1 , null)
        mMap.addMarker(

            MarkerOptions()
                .position(LatLng(lat,long))
                .title("Wydarzenie")
                .icon(EventMethods.getMarkerIcon(color)))
    }


}
