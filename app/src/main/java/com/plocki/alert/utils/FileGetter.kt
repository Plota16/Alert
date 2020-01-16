package com.plocki.alert.utils

import android.content.ContentResolver
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import java.lang.Exception

class FileGetter {
    companion object {
        fun getRealPath(uri: Uri, contentResolver: ContentResolver): String {
            var realPath = ""
            try {

                val proj = arrayOf(MediaStore.Images.Media.DATA)
                val cursor = contentResolver.query(uri, proj, null, null, null, null)
                if (cursor!!.moveToFirst()) {
                    val column = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
                    realPath = cursor.getString(column)
                }
                cursor.close()
            } catch (e: Exception) {
                Log.e("FILE ERROR", "Nie można pobrać prawdziwej ścieżki zdjęcia")
            }
            return realPath
        }
    }
}