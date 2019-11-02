package com.plocki.alert.models

import android.location.Location
import com.google.android.gms.maps.model.LatLng

class Global {

    lateinit var location: Location

    var list = listOf(
        Event(
            1,
            LatLng(52.39786111,16.92500000),
            "https://6.allegroimg.com/original/0cf9f4/7082cd20499c8374de45a7de79e6",
            "Stłuczka",
            "opis",
            "Korek",
            "Plota"
        ),
        Event(
            2,
            LatLng(53.39786111,17.92500000),
            "https://e.allegroimg.com/original/0cae51/a3e07be24e8cbdc2cf251cedbe7",
            "Koncert",
            "opis2",
            "Zespół",
            "Stupnicki"
        )
    )


    var mapHashMap = HashMap<String, Event>()

    var listHashMap = HashMap<String, Event>()



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