package com.plocki.alert.activities

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.plocki.alert.R

class LocationPicker : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap :GoogleMap
    private lateinit var lastLocation: Location
    private var hasInitLocation = false
    private var lat = 0.0
    private var long = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_location_picker)

        val extra = intent.getStringExtra("Coords")
        if(extra.isNotEmpty()){
            hasInitLocation = true
            val tab = extra.split("+")
            lat = tab[0].toDouble()
            long = tab[1].toDouble()
        }

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.fragpick) as SupportMapFragment
        mapFragment.getMapAsync(this)

    }

    override fun onMapReady(googleMap: GoogleMap) {

        mMap = googleMap
        mMap.mapType = GoogleMap.MAP_TYPE_NORMAL

        mMap.clear()
        if(hasInitLocation){
            mMap.addMarker(
                MarkerOptions()
                    .position(LatLng(lat,long))
                    .title("Lokalizacja")
            )
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED) {
            mMap.isMyLocationEnabled = true
        } else {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
                mMap.isMyLocationEnabled = true

            } else {

            }
        }

        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        fusedLocationClient.lastLocation.addOnSuccessListener {
            if (it != null) {
                lastLocation = it
                val currentLatLng = LatLng(it.latitude, it.longitude)
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 16f), 1 , null)
            }
        }


    }

    fun onPick(v: View?) {
        val center = mMap.cameraPosition.target
        val intent = Intent()
        val res = center.latitude.toString() +"+"+center.longitude.toString()
        intent.data = Uri.parse(res)
        setResult(Activity.RESULT_OK,intent)
        finish()
    }

}
