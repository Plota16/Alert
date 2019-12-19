package com.plocki.alert.models

import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.media.ThumbnailUtils
import android.net.Uri
import android.provider.MediaStore
import com.google.android.gms.maps.model.LatLng
import kotlin.math.roundToInt
import android.location.Location
import android.location.LocationManager
import android.net.ConnectivityManager
import com.jaychang.sa.Initializer
class EventMethods {

    companion object {

        fun isGpsOn(): Boolean {
            val locationManager = Initializer.context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
            return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        }
        fun isNetworkOn(context: Context): Boolean {
            val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val networkInfo = connectivityManager.activeNetworkInfo
            return networkInfo != null && networkInfo.isConnected
        }




        fun thumbnailFromUri(context: Context, uri : Uri?): BitmapDrawable {
            val filePathColumn = arrayOf(MediaStore.Images.Media.DATA)
            val cursor = context.contentResolver.query(uri!!, filePathColumn, null, null, null)
            cursor!!.moveToFirst()

            val columnIndex = cursor.getColumnIndex(filePathColumn[0])
            val picturePath = cursor.getString(columnIndex)
            cursor.close()
            val mBitmap = ThumbnailUtils.extractThumbnail(
                BitmapFactory.decodeFile(picturePath),
                256, 256
            )

            return BitmapDrawable(context.resources, mBitmap)


        }


        fun calcDistance(point: LatLng) : Int{
            val inst = Global.getInstance()

            val pointLocation = Location("")
            pointLocation.longitude = point.longitude
            pointLocation.latitude = point.latitude
            val currLocation = inst!!.userLocation

            val distance = currLocation.distanceTo(pointLocation)



            return distance.roundToInt()
        }



        fun getMaxDistance(dist : String): Int{
            when(dist){
                "Nielimitowane" -> return 0
                "500 m" -> return 500
                "1 km" -> return 1000
                "3 km" -> return 3000
                "5 km" -> return 5000
                "10 km" -> return 10000
                "20 km" -> return 20000
                "100 km" -> return 100000
            }
            return 0
        }

    }
}