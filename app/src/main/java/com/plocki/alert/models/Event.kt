package com.plocki.alert.models

import android.graphics.drawable.BitmapDrawable
import com.google.android.gms.maps.model.LatLng

class Event (
    var id : Int,
    var coords: LatLng,
    var image: String,
    var title: String,
    var desctription : String,
    var category: String,
    var creator : String


)

