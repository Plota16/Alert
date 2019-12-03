package com.plocki.alert.models

import android.location.Location
import android.view.View
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import kotlinx.android.synthetic.main.fragment_list.*
import java.util.*
import java.util.concurrent.locks.ReentrantLock
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import kotlin.properties.Delegates

class Global {
    var changed = false
    var bool = true
    lateinit var location: Location
    lateinit var view : View

    var cameraPos: LatLng = LatLng(0.0,0.0)
    var toAdd = ArrayList<Event>()
    var toRemove = ArrayList<Event>()
    var list = arrayListOf(
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

    var CategoryList = arrayOf("Wydarzenie",
    "Korek",
    "Wypadek",
    "Utrudnienia")

    var FilterList = booleanArrayOf(true,true,true,true)

    val distanceList = arrayOf("Nielimitowane", "500 m", "1 km", "3 km", "5 km", "10 km", "20 km", "100 km")

    var mapHashMap = HashMap<String, Event>()
    var listHashMap = HashMap<String, Event>()

    var filterdDistnance = "Nielimitowane"

    companion object {
        val ip = "192.168.1.104"
        private var mInstance: Global? = null

            @Synchronized fun getInstance(): Global? {
                if (null == mInstance) {
                    mInstance = Global()
                }
                return mInstance
            }
    }

}