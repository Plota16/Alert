package com.plocki.alert.models

import android.location.Location
import android.view.View
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import java.util.*
import kotlin.collections.HashMap

class Global {

    lateinit var location: Location
    lateinit var view : View
    var cameraPos: LatLng = LatLng(0.0,0.0)

    var list = listOf(
        Event(
            1,
            UUID.randomUUID(),
            LatLng(52.39786111,16.92500000),
            "https://6.allegroimg.com/original/0cf9f4/7082cd20499c8374de45a7de79e6",
            "Stłuczka",
            "opis opis opis opis opis opis",
            1,
            1
        ),
        Event(
            2,
            UUID.randomUUID(),
            LatLng(53.39786111,17.92500000),
            "https://e.allegroimg.com/original/0cae51/a3e07be24e8cbdc2cf251cedbe7e",
            "Koncert",
            "opis2",
            2,
            2
        ),
        Event(
            3,
            UUID.randomUUID(),
            LatLng(52.59786111,16.72500000),
            "https://e.allegroimg.com/original/0cae51/a3e07be24e8cbdc2cf251cedbe7",
            "Roboty drogowe",
            "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Proin nibh augue, suscipit a, scelerisque sed, lacinia in, mi. Cras vel lorem",
            3,
            2
        ),
        Event(
            4,
            UUID.randomUUID(),
            LatLng(52.39796111,16.93500000),
            "https://6.allegroimg.com/original/0cf9f4/7082cd20499c8374de45a7de79e6",
            "Stłuczka",
            "opis opis opis opis opis opis",
            1,
            1
        ),
        Event(
            5,
            UUID.randomUUID(),
            LatLng(52.39796111,16.92500000),
            "https://6.allegroimg.com/original/0cf9f4/7082cd20499c8374de45a7de79e6",
            "Stłuczka",
            "opis opis opis opis opis opis",
            1,
            1
        ),
        Event(
            6,
            UUID.randomUUID(),
            LatLng(52.39786111,16.93500000),
            "https://6.allegroimg.com/original/0cf9f4/7082cd20499c8374de45a7de79e6",
            "Stłuczka",
            "opis opis opis opis opis opis",
            1,
            1
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