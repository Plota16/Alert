package com.plocki.alert.models

import android.location.Location
import com.google.android.gms.maps.model.LatLng
import java.util.*
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
    var eventList = arrayListOf(
        Event(
            UUID.randomUUID(),
            LatLng(52.39786111,16.92500000),
            "Stłuczka",
            "https://6.allegroimg.com/original/0cf9f4/7082cd20499c8374de45a7de79e6",
            "opis opis opis opis opis opis",
            1,
            1
        )

    )

    var categoryList = arrayOf("Wydarzenie",
        "Korek",
        "Wypadek",
        "Utrudnienia")

    var filterList = booleanArrayOf(true,true,true,true)

    val distanceList = arrayOf("Nielimitowane", "500 m", "1 km", "3 km", "5 km", "10 km", "20 km", "100 km")

    var currentDistanceFilter = "Nielimitowane"

    var mapHashMap = HashMap<String, Event>()
    var listHashMap = HashMap<String, Event>()



    companion object {
        const val ip = "192.168.1.187"
        private var mInstance: Global? = null

        @Synchronized fun getInstance(): Global? {
            if (null == mInstance) {
                mInstance = Global()
            }
            return mInstance
        }
    }

}