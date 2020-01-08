package com.plocki.alert.adapters

import android.content.Context
import com.google.maps.android.clustering.Cluster
import com.google.maps.android.clustering.ClusterManager
import com.google.android.gms.maps.GoogleMap
import com.google.maps.android.clustering.view.DefaultClusterRenderer
import com.google.android.gms.maps.model.MarkerOptions



class MarkerClusterRenderer(
    context: Context,
    googleMap: GoogleMap,
    clusterManager: ClusterManager<MarkerClusterItem>
) : DefaultClusterRenderer<MarkerClusterItem>(context, googleMap, clusterManager) {

    override fun shouldRenderAsCluster(cluster: Cluster<MarkerClusterItem>?): Boolean {
        return cluster!!.size > 1
    }

    override fun onBeforeClusterItemRendered(item: MarkerClusterItem, markerOptions: MarkerOptions?) {
        markerOptions!!.icon(item.icon)
        markerOptions.visible(true)
    }
}