package com.plocki.alert.models

import android.location.Location
import com.google.android.gms.maps.model.LatLng
import java.lang.reflect.Array
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class Global {


    //State Variables
    var isErrorActivityOpen = false
    var isUserSigned = false
    var isDataChanged = false
    var isDataLoadedFirstTime = true
    var isMapCreated = true



    //User Variables
    var userToken: String = ""
    var userName: String = ""
    var userCameraPosition: LatLng = LatLng(52.39786111,16.92500000)
    lateinit var userLocation: Location

    // Lists
    var eventList = arrayListOf<Event>()
    var categoryList = arrayListOf<String>()

    val distanceList = arrayOf("Nielimitowane", "500 m", "1 km", "3 km", "5 km", "10 km", "20 km", "100 km")

    var currentDistanceFilter = "Nielimitowane"

    var mapHashMap = HashMap<String, Event>()
    var listHashMap = HashMap<String, Event>()
    var categoryHashMap= HashMap<String, Category>()
    val filterHashMap = HashMap<String, Boolean>()
    val titleUUIDHashMap = HashMap<String, String>()



    companion object {
        const val ip = "alert-api-staging.eu-central-1.elasticbeanstalk.com"
        private var mInstance: Global? = null

        @Synchronized fun getInstance(): Global? {
            if (null == mInstance) {
                mInstance = Global()
            }
            return mInstance
        }
    }

}