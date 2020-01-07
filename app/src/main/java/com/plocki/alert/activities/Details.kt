package com.plocki.alert.activities

import android.Manifest
import android.app.Dialog
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.Window
import android.widget.Button
import androidx.core.content.ContextCompat
import com.apollographql.apollo.ApolloCall
import com.apollographql.apollo.api.Response
import com.apollographql.apollo.exception.ApolloException
import com.bumptech.glide.Glide
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.MarkerOptions
import com.google.gson.GsonBuilder
import com.plocki.alert.*
import com.plocki.alert.API.modules.*
import com.plocki.alert.models.Event
import com.plocki.alert.models.Global
import com.plocki.alert.models.LikeType
import com.plocki.alert.models.Report
import com.plocki.alert.type.CreateLikeDto
import com.plocki.alert.type.DeleteLikeDto
import com.plocki.alert.type.LikeEnum
import com.plocki.alert.utils.HttpErrorHandler
import kotlinx.android.synthetic.main.activity_details.*
import kotlinx.android.synthetic.main.dialog_report.*
import kotlinx.android.synthetic.main.likebar.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlin.math.roundToInt


class Details : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var rate: LikeType

    private lateinit var mMap : GoogleMap
    private lateinit var event : Event
    private var inst = Global.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_details)

        fetchReportsCategories()

        thump_up.setOnClickListener {
            likeClicked()
        }
        thumb_down.setOnClickListener{
            dislikeClicked()
        }

        val bundle :Bundle ?=intent.extras

        val listMarker: String? = bundle!!.getString("pos")
        val extraMarker : String? = bundle.getString("Marker")


        if(extraMarker == null){
            this.event = inst!!.listHashMap[listMarker]!!
        }
        else if(listMarker == null){
            this.event = inst!!.mapHashMap[extraMarker]!!
        }

        setPreviousRateButton()
        details_category.text = event.category.title
        details_desc.text = event.description
        supportActionBar!!.title = event.title
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        val display = windowManager.defaultDisplay
        val outMetrics = DisplayMetrics()
        display.getMetrics(outMetrics)

        val dpWidth = outMetrics.widthPixels
        val dpHeight = dpWidth.toDouble()/4*3


        details_image.layoutParams.width = dpWidth
        details_image.layoutParams.height = dpHeight.roundToInt()
        details_image.requestLayout()

        Glide.with(this)
            .load("${Global.photoBaseDomain}${event.image}.jpg")
            .placeholder(R.drawable.placeholder)
            .centerCrop()
            .override(dpWidth ,dpHeight.roundToInt())
            .into(details_image)

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.details_map) as SupportMapFragment
        mapFragment.getMapAsync(this)


    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.details_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId

        if (id == android.R.id.home) {
            finish()
        }
        else if(id == R.id.action_report){
            showReportDialog()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onMapReady(p0: GoogleMap?) {
        mMap = p0!!
        mMap.mapType = GoogleMap.MAP_TYPE_NORMAL

        mMap.clear() //clear old markers

        mMap.uiSettings.isScrollGesturesEnabled = false
        mMap.uiSettings.isCompassEnabled = false
        mMap.uiSettings.isMyLocationButtonEnabled = false
        mMap.uiSettings.isZoomGesturesEnabled = false
        mMap.uiSettings.isZoomControlsEnabled = true

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED) {
            mMap.isMyLocationEnabled = true
        }
        else {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
                mMap.isMyLocationEnabled = true

            }
        }
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(event.coordinates, 15f),1, null)
        fusedLocationClient.lastLocation.addOnSuccessListener {
            if (it != null) {
                Add.lastLocation = it
            }
        }


        mMap.addMarker(
            MarkerOptions()
                .position(event.coordinates)
        )
    }

    private fun setPreviousRateButton() {
        rate = event.userLike!!
        println(rate)
        doColor()
    }

    private fun likeClicked(){
        if(rate != LikeType.LIKE){
            rate = LikeType.LIKE
            giveRate()
        }
        else if(rate == LikeType.LIKE && rate != LikeType.DISLIKE){
            rate = LikeType.NONE
            removeRate()
        }
        doColor()
    }

    private fun createReport(report: Report) {

        val createReportDto = report.createReportDto(this)
        ReportsApi.createReport(
            createReportDto,
            object : ApolloCall.Callback<CreateReportMutation.Data>() {
                override fun onFailure(e: ApolloException) {
                    HttpErrorHandler.handle(500)
                }

                override fun onResponse(response: Response<CreateReportMutation.Data>) {
                    Log.d("SUCCESS", response.data().toString())
                    if (response.hasErrors()) {
                        Log.e(
                            "ERROR ",
                            response.errors()[0].customAttributes()["statusCode"].toString()
                        )
                        val gson = GsonBuilder().create()
                        val errorMap =
                            gson.fromJson(response.errors()[0].message(), Map::class.java)
                        HttpErrorHandler.handle(errorMap["statusCode"].toString().toFloat().toInt())
                        return
                    }

                }
            }
        )
    }



    private fun fetchReportsCategories() {

        ReportCategoriesApi.fetchReportCategories(object :
            ApolloCall.Callback<AllReportCategoriesQuery.Data>() {
            override fun onResponse(response: Response<AllReportCategoriesQuery.Data>) {
                if (response.hasErrors()) {
                    val gson = GsonBuilder().create()
                    val errorMap = gson.fromJson(response.errors()[0].message(), Map::class.java)
                    HttpErrorHandler.handle(errorMap["statusCode"].toString().toFloat().toInt())
                    return
                }
                val reports = response.data()!!.reportCategories()
                Global.getInstance()!!.reportList.clear()
                for(report in reports){
                    Global.getInstance()!!.reportList.add(report.title())
                    Global.getInstance()!!.reportHashMap.put(report.title(), report.uuid().toString())
                }
                println("REPORT CATEGORIES " + response.data().toString())
            }

            override fun onFailure(e: ApolloException) {

                HttpErrorHandler.handle(500)
            }

        })
    }

    private fun removeRate() {
        val deleteLikeDto: DeleteLikeDto = DeleteLikeDto.builder()
            .eventUuid(event.UUID.toString())
            .build()

        LikesApi.deleteLike(
            deleteLikeDto,
            object: ApolloCall.Callback<DeleteLikeMutation.Data>() {
                override fun onFailure(e: ApolloException) {
                    HttpErrorHandler.handle(500)
                    setPreviousRateButton()
                }

                override fun onResponse(response: Response<DeleteLikeMutation.Data>) {
                    Log.d("SUCCESS", response.data().toString())
                    if (response.hasErrors()) {
                        Log.e("ERROR ", response.errors()[0].customAttributes()["statusCode"].toString())
                        val gson = GsonBuilder().create()
                        val errorMap = gson.fromJson(response.errors()[0].message(), Map::class.java)
                        HttpErrorHandler.handle(errorMap["statusCode"].toString().toFloat().toInt())
                        return
                    }
                    else{
                        FetchEventsHandler.fetchEvents()
                    }
                }
            }
        )

    }

    private fun giveRate() {
        var createLikeDto: CreateLikeDto = CreateLikeDto.builder()
            .like(LikeEnum.LIKE)
            .eventUuid(event.UUID.toString())
            .build()
        if (rate == LikeType.DISLIKE) {
            createLikeDto =  CreateLikeDto.builder()
                .like(LikeEnum.DISLIKE)
                .eventUuid(event.UUID.toString())
                .build()
        }
        LikesApi.createOrUpadateLike(
            createLikeDto,
            object : ApolloCall.Callback<CreateOrUpdateLikeMutation.Data>() {
                override fun onFailure(e: ApolloException) {
                    HttpErrorHandler.handle(500)
                    setPreviousRateButton()
                }

                override fun onResponse(response: Response<CreateOrUpdateLikeMutation.Data>) {
                    Log.d("SUCCESS", response.data().toString())
                    if (response.hasErrors()) {
                        Log.e("ERROR ", response.errors()[0].customAttributes()["statusCode"].toString())
                        val gson = GsonBuilder().create()
                        val errorMap = gson.fromJson(response.errors()[0].message(), Map::class.java)
                        HttpErrorHandler.handle(errorMap["statusCode"].toString().toFloat().toInt())
                        return
                    }
                    else{
                        FetchEventsHandler.fetchEvents()
                    }
                }
            })
    }

    private fun dislikeClicked(){
        if(rate != LikeType.DISLIKE){
            rate = LikeType.DISLIKE
            giveRate()
        }
        else {
            rate = LikeType.NONE
            removeRate()
        }
        doColor()
    }

    private fun doColor(){
        if(rate == LikeType.NONE){
            thumb_down.foreground.alpha = 255
            thump_up.foreground.alpha = 255
        }
        else if(rate == LikeType.LIKE){
            thump_up.foreground.alpha = 255
            thumb_down.foreground.alpha = 128
        }
        else if(rate == LikeType.DISLIKE){
            thump_up.foreground.alpha = 128
            thumb_down.foreground.alpha = 255
        }
    }

    private fun showReportDialog() {
        val dialog = Dialog(this)
        dialog .requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog .setCancelable(false)
        dialog .setContentView(R.layout.dialog_report)

        val yesBtn = dialog .findViewById(R.id.report_ok) as Button
        val noBtn = dialog .findViewById(R.id.report_cancel) as Button
        yesBtn.setOnClickListener {
            dialog .dismiss()
        }
        noBtn.setOnClickListener {
            val cat = reportCategory_in.text.toString()
            val uuid = Global.getInstance()!!.reportHashMap.get(cat)!!
            val desc = desc2.text.toString()
            createReport(Report(description = desc,category = uuid, event = event))
            dialog .dismiss()
        }
        dialog .show()

    }
}

