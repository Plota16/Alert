package com.plocki.alert.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.plocki.alert.MyApplication
import com.plocki.alert.R
import com.plocki.alert.models.EventMethods
import com.plocki.alert.models.Global
import kotlinx.android.synthetic.main.activity_connection_error.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import com.google.android.gms.location.LocationSettingsStatusCodes
import android.content.IntentSender
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationSettingsRequest
import android.util.Log
import android.content.Intent
import android.provider.Settings
import androidx.appcompat.app.AlertDialog
import com.jaychang.sa.Initializer.context


class ConnectionError : AppCompatActivity() {

    private val requestCheckSettings = 214
    private var gpsEnabled = false
    private var networkEnabled = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_connection_error)

        networkButton.setOnClickListener {
            onConnectNetworkClick()
        }

        gpsButton.setOnClickListener {
            onConnectGpsClick()
        }

        GlobalScope.launch(context = Dispatchers.Main) {
            while (true){
                try{
                    gpsEnabled = EventMethods.isGpsOn()
                    networkEnabled = EventMethods.isNetworkOn(MyApplication.getAppContext())
                }catch (ex: Exception ){}

                if(gpsEnabled && networkEnabled){
                    Global.getInstance()!!.isErrorActivityOpen = false
                    finish()
                }

                if(gpsEnabled){
                    gpsLayout.visibility = View.INVISIBLE
                }
                else{
                    gpsLayout.visibility = View.VISIBLE
                }
                if(networkEnabled){
                    networkLayout.visibility = View.INVISIBLE
                }
                else{
                    networkLayout.visibility = View.VISIBLE
                }
                delay(2000)
            }
        }
    }


    override fun onBackPressed(){
    }

    private fun onConnectNetworkClick(){
        val alertDialogBuilder = AlertDialog.Builder(this,R.style.CustomDialogTheme)
        alertDialogBuilder
            .setMessage(R.string.internet_dialog_text)
            .setTitle(R.string.internet_dialog_title)
            .setCancelable(false)
            .setPositiveButton(R.string.add_dialog_positive){
                    _, _ ->
                val dialogIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(dialogIntent)
            }


        alertDialogBuilder.setNegativeButton(R.string.add_dialog_negative) {
                dialog, _ ->
            dialog.cancel()
    }

    val alert = alertDialogBuilder.create()
    alert.show()
    }

    private fun onConnectGpsClick(){
        val builder = LocationSettingsRequest.Builder()
        builder.addLocationRequest(LocationRequest().setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY))
        builder.setAlwaysShow(true)
        val mLocationSettingsRequest = builder.build()

        val mSettingsClient = LocationServices.getSettingsClient(this@ConnectionError)

        mSettingsClient
            .checkLocationSettings(mLocationSettingsRequest)
            .addOnSuccessListener {
                //Success Perform Task Here
            }
            .addOnFailureListener { e ->
                when ((e as ApiException).statusCode) {
                    LocationSettingsStatusCodes.RESOLUTION_REQUIRED -> try {
                        val rae = e as ResolvableApiException
                        rae.startResolutionForResult(this@ConnectionError, requestCheckSettings)
                    } catch (sie: IntentSender.SendIntentException) {
                        Log.e("GPS", "Unable to execute request.")
                    }

                    LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE -> Log.e(
                        "GPS",
                        "Location settings are inadequate, and cannot be fixed here. Fix in Settings."
                    )
                }
            }
            .addOnCanceledListener {
                Log.e(
                    "GPS",
                    "checkLocationSettings -> onCanceled"
                )
            }
    }

}
