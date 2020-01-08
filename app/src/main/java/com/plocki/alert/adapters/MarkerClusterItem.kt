package com.plocki.alert.adapters

import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.maps.android.clustering.ClusterItem


class MarkerClusterItem(markerOptions: MarkerOptions) : ClusterItem {

    private var title = markerOptions.title
    private var latLng:LatLng = markerOptions.position
    var icon: BitmapDescriptor = markerOptions.icon

    override fun getPosition(): LatLng {
        return latLng
    }

    override fun getTitle(): String {
        return title
    }


    override fun getSnippet(): String {
        return ""
    }
}