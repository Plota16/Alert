package com.plocki.alert.adapters

import android.app.Activity
import android.content.Context
import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat

import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.Marker
import com.plocki.alert.R

class CustomInfoWindowGoogleMap(private val context: Context) : GoogleMap.InfoWindowAdapter {

    override fun getInfoWindow(marker: Marker): View? {
        return null
    }

    override fun getInfoContents(marker: Marker): View {
        val view = (context as Activity).layoutInflater
            .inflate(R.layout.info_window, null)

        val category = view.findViewById<TextView>(R.id.infoWindowCategory)
        val title = view.findViewById<TextView>(R.id.infoWindowTitle)
        val like = view.findViewById<TextView>(R.id.infoWindowLike)


        val infoContainer =
            marker.title.split("~".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()

        title.text = infoContainer[1]
        category.text = infoContainer[0].toUpperCase()


        if (Integer.parseInt(infoContainer[2]) > 0) {
            val likeContainer = "+" + infoContainer[2]
            like.text = likeContainer
            like.setTextColor(ContextCompat.getColor(context,R.color.green))
        } else if (Integer.parseInt(infoContainer[2]) < 0) {
            val likeContainer = "-" + infoContainer[2]
            like.text = likeContainer
            like.setTextColor(ContextCompat.getColor(context,R.color.red))
        } else {
            like.text = infoContainer[2]
        }
        return view
    }
}