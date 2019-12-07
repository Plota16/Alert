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

    var errorActivityOpen = false

    var logged = false
    var changed = false
    var bool = true
    lateinit var location: Location
    lateinit var view : View

    var token: String? = null
        //"eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1dWlkIjoiYzk0ZjRjNDAtZjFkYy00OTVhLTkxZmItY2E4ZTRkZDFhNjllIiwidG9rZW5JZCI6NTI0ODc0MCwiaWF0IjoxNTc1NDAwODU3LCJleHAiOjE1NzY2OTY4NTd9.yfoAei97Lf431grJRTtR3gm0x3MBET-kK_3bSINdm_U"
    var username: String = ""
    var cameraPos: LatLng = LatLng(0.0,0.0)

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

    var filterdDistnance = "Nielimitowane"

    var mapHashMap = HashMap<String, Event>()
    var listHashMap = HashMap<String, Event>()

    companion object {
        val ip = "192.168.1.74"
        private var mInstance: Global? = null

            @Synchronized fun getInstance(): Global? {
                if (null == mInstance) {
                    mInstance = Global()
                }
                return mInstance
            }
    }

}