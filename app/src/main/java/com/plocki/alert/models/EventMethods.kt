package com.plocki.alert.models

import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.media.ThumbnailUtils
import android.net.Uri
import android.provider.MediaStore
import com.google.android.gms.maps.model.LatLng
import kotlin.math.PI
import kotlin.math.roundToInt
import android.R.attr.src
import android.location.Location
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL


class EventMethods {

    companion object {
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

        fun imageFromURL(context: Context, src : String): BitmapDrawable? {
            try {
                val url = URL(src)
                val connection = url.openConnection() as HttpURLConnection
                connection.doInput = true
                connection.connect()
                val input = connection.inputStream
                val bitmap = BitmapFactory.decodeStream(input)
                return BitmapDrawable(context.resources, bitmap)
            } catch (e: IOException) {
                // Log exception
                return null
            }

        }

        fun calcDistance(point: LatLng) : Int{

            val inst = Global.getInstance()

            var pointLocation = Location("")
            pointLocation.longitude = point.longitude
            pointLocation.latitude = point.latitude
            var currLocation = inst!!.location

            var distance = currLocation.distanceTo(pointLocation)



            return distance.roundToInt()
        }
    }
}