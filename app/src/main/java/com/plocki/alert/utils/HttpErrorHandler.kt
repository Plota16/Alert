package com.plocki.alert.utils

import android.util.Log
import android.widget.Toast
import com.plocki.alert.models.Global

object HttpErrorHandler {
    fun handle(statusCode: Int) {
        Log.e("ERROR CODE", statusCode.toString())
        val currentActivity = Global.getInstance()!!.currentActivity!!
        when (statusCode) {
            401 -> {
                AppLauncher.logOut()
            }
            500 -> {
                currentActivity.runOnUiThread {
                    Toast.makeText(currentActivity, "Nie można nawiązać połączenia z serwerem", Toast.LENGTH_SHORT).show()
                }
            }
            else -> Log.e("ERROR CODE", "NIEZNANY")

        }
    }
}