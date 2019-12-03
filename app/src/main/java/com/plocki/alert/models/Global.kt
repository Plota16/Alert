package com.plocki.alert.models

import android.location.Location
import android.view.View
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import java.util.*
import kotlin.collections.HashMap

class Global {

    var bool = true
    lateinit var location: Location
    lateinit var view : View
    var cameraPos: LatLng = LatLng(0.0,0.0)

    var list = arrayListOf(
        Event(
            UUID.randomUUID(),
            LatLng(52.39786111,16.92500000),
            "https://6.allegroimg.com/original/0cf9f4/7082cd20499c8374de45a7de79e6",
            "Stłuczka",
            "opis opis opis opis opis opis",
            1,
            1
        )
//        Event(
//            2,
//            UUID.randomUUID(),
//            LatLng(53.39786111,17.92500000),
//            "https://e.allegroimg.com/original/0cae51/a3e07be24e8cbdc2cf251cedbe7e",
//            "Koncert",
//            "opis2",
//            2,
//            2
//        ),
//        Event(
//            3,
//            UUID.randomUUID(),
//            LatLng(52.59786111,16.72500000),
//            "https://images.freeimages.com/images/large-previews/7e6/mediterranean-food-1311330.jpg",
//            "Roboty drogowe",
//            "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Proin nibh augue, suscipit a, scelerisque sed, lacinia in, mi. Cras vel lorem",
//            3,
//            2
//        ),
//        Event(
//            4,
//            UUID.randomUUID(),
//            LatLng(52.39796511,16.93500000),
//            "https://images.freeimages.com/images/large-previews/cf7/apples-1324784.jpg",
//            "Stłuczka",
//            "opis opis opis opis opis opis",
//            1,
//            1
//        ),
//        Event(
//            5,
//            UUID.randomUUID(),
//            LatLng(52.39496111,16.94500000),
//            "https://images.freeimages.com/images/large-previews/751/orange-splash-1326817.jpg",
//            "Stłuczka",
//            "opis opis opis opis opis opis",
//            1,
//            1
//        ),
//        Event(
//            6,
//            UUID.randomUUID(),
//            LatLng(52.37756211,16.93500000),
//            "https://6.allegroimg.com/original/0cf9f4/7082cd20499c8374de45a7dee6",
//            "Stłuczka",
//            "opis opis opis opis opis opis",
//            1,
//            1
//        )
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
        private var mInstance: Global? = null

            @Synchronized fun getInstance(): Global? {
                if (null == mInstance) {
                    mInstance = Global()
                }
                return mInstance
            }
    }
}