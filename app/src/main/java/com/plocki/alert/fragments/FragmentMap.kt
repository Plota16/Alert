package com.plocki.alert.fragments

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import androidx.fragment.app.Fragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.CameraUpdateFactory
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.Marker
import com.plocki.alert.activities.Details
import com.plocki.alert.models.Global
import com.plocki.alert.R
import com.plocki.alert.models.EventMethods
import kotlinx.android.synthetic.main.fragment_list.*
import kotlinx.android.synthetic.main.fragment_map.*
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class FragmentMap : Fragment(), OnMapReadyCallback, GoogleMap.OnInfoWindowClickListener {


    private var hasMapBeenCreated = false
    private var isFilteringPossible = false

    private lateinit var mMap: GoogleMap
    private lateinit var lastLocation: Location
    private var inst = Global.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        retainInstance = true
        super.onActivityCreated(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        newEventsMap.setOnClickListener{
            updateMap()
            Global.getInstance()!!.isDataChanged = false
            newEventsMap.visibility = View.GONE
        }

        GlobalScope.launch(context = Main) {
            while (true){
                    if(Global.getInstance()!!.isDataChanged){
                        newEventsMap.visibility = View.VISIBLE
                    }
                    else{
                        newEventsMap.visibility = View.GONE
                    }

                delay(2000)
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_map, container, false)
        val mapFragment = childFragmentManager.findFragmentById(R.id.frg) as SupportMapFragment?
        mapFragment!!.getMapAsync(this)
        return rootView
    }

    override fun onMapReady(p0: GoogleMap?) {
        mMap = p0!!
        mMap.setOnInfoWindowClickListener(this)
        mMap.mapType = GoogleMap.MAP_TYPE_NORMAL
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(inst!!.userCameraPosition, 12f), 1, null)
        mMap.clear() //clear old markers

        if (ContextCompat.checkSelfPermission(this.context!!, Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED
        ){
            mMap.isMyLocationEnabled = true
        } else {
            if (ContextCompat.checkSelfPermission(this.context!!, Manifest.permission.ACCESS_COARSE_LOCATION)
                == PackageManager.PERMISSION_GRANTED
            ) {
                mMap.isMyLocationEnabled = true

            }
        }

        val context = this.context

        mMap.setOnCameraMoveListener {
            inst!!.userCameraPosition = mMap.cameraPosition.target
        }


        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context!!)

        fusedLocationClient.lastLocation.addOnSuccessListener {
            if (it != null) {
                lastLocation = it
                inst!!.userLocation = it
                val currentLatLng = LatLng(it.latitude, it.longitude)

                if(inst!!.userCameraPosition == LatLng(52.39786111,16.92500000)){
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 12f), 1, null)
                }
            }
        }
        updateMap()
    }

    override fun onInfoWindowClick(p0: Marker?) {
        val intent = Intent(activity, Details::class.java)
        intent.putExtra("Marker", p0!!.id)
        startActivity(intent)
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        updateMap()
    }

    override fun onResume() {
        super.onResume()
        if(hasMapBeenCreated){
            updateMap()
        }
        else{
            hasMapBeenCreated = true
        }
    }

    private fun updateMap() {
        mMap.clear()
        for (event in inst!!.eventList) {

            val index = inst!!.categoryList.indexOf(EventMethods.getCategory(event.category))

            if(isFilteringPossible) {
                if(inst!!.filterList[index]){
                    if(EventMethods.calcDistance(event.coords) < EventMethods.getMaxDistance(inst!!.currentDistanceFilter) || EventMethods.getMaxDistance(inst!!.currentDistanceFilter) == 0) {
                        val marker = mMap.addMarker(
                            MarkerOptions()
                                .position(event.coords)
                                .title(event.title)
                        )
                        inst!!.mapHashMap[marker.id] = event
                    }
                }
            }
            else{
                val marker = mMap.addMarker(
                    MarkerOptions()
                        .position(event.coords)
                        .title(event.title)
                )
                inst!!.mapHashMap[marker.id] = event

            }
        }
        isFilteringPossible = true
    }
}