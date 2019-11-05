package com.plocki.alert.models

import android.graphics.drawable.BitmapDrawable
import com.google.android.gms.maps.model.LatLng
import java.util.*

class Event (
    var Id : Int,
    var UUID : UUID,
    var coords: LatLng,
    var image: String,
    var title: String,
    var desctription : String?,
    var category: Int,
    var creator : Int


)

