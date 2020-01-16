package com.plocki.alert.activities

import android.Manifest
import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.*
import android.widget.Button
import android.widget.ImageView
import android.widget.RelativeLayout
import androidx.core.content.ContextCompat
import com.apollographql.apollo.ApolloCall
import com.apollographql.apollo.api.Response
import com.apollographql.apollo.exception.ApolloException
import com.bumptech.glide.Glide
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.gson.GsonBuilder
import com.plocki.alert.*
import com.plocki.alert.API.modules.*
import com.plocki.alert.models.*
import com.plocki.alert.type.CreateLikeDto
import com.plocki.alert.type.DeleteLikeDto
import com.plocki.alert.type.LikeEnum
import com.plocki.alert.utils.HttpErrorHandler
import kotlinx.android.synthetic.main.activity_details.*
import kotlinx.android.synthetic.main.likebar.*
import kotlin.math.roundToInt


class Details : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var rate: LikeType

    private lateinit var mMap : GoogleMap
    private lateinit var event : Event
    private var inst = Global.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_details)

        setupExtras()
        fetchReportsCategories()
        setOnClickListeners()
        setPreviousRateButton()
        setupImage()

        details_category.text = event.category.title
        if(event.description == ""){
            desc_lay.visibility = View.GONE
        }
        else{
            details_desc.text = event.description
        }

        supportActionBar!!.title = event.title
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)




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
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(event.coordinates, 14f), 1, null)
        setupMapSettings()
        turnOnMyLocation()
        initMarker()
        setupOnMapClickListener()


    }

    private fun setOnClickListeners(){
        thump_up.setOnClickListener {
            likeClicked()
        }
        thumb_down.setOnClickListener{
            dislikeClicked()
        }
        details_image.setOnClickListener {
            if(event.image != null){
                showImage()
            }
        }

    }

    private fun setupExtras(){
        val bundle :Bundle ?=intent.extras

        val listMarker: String? = bundle!!.getString("pos")
        val extraMarker : String? = bundle.getString("Marker")


        if(extraMarker == null){
            this.event = inst!!.listHashMap[listMarker]!!
        }
        else if(listMarker == null){
            this.event = inst!!.mapHashMap[extraMarker]!!
        }
    }

    private fun setupImage(){
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
    }

    //MAP

    private fun initMarker(){
        mMap.addMarker(
            MarkerOptions()
                .position(event.coordinates)
                .icon(EventMethods.getMarkerIcon(event.category.color))
        )
    }

    private fun setupMapSettings(){
        mMap.clear()
        mMap.mapType = GoogleMap.MAP_TYPE_NORMAL
        mMap.uiSettings.isScrollGesturesEnabled = false
        mMap.uiSettings.isCompassEnabled = false
        mMap.uiSettings.isMyLocationButtonEnabled = false
        mMap.uiSettings.isZoomGesturesEnabled = false
    }

    private fun setupOnMapClickListener(){
        mMap.setOnMapClickListener {
            val intent = Intent(this@Details, Location::class.java)
            val extra = event.coordinates.latitude.toString()+"~~"+event.coordinates.longitude.toString()+"~~"+event.category.color+"~~"+event.title
            intent.putExtra("cords",extra)
            startActivity(intent)
        }
    }

    private fun turnOnMyLocation(){
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
    }



    //LIKES

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
        when (rate) {
            LikeType.NONE -> {
                thumb_down.foreground.alpha = 255
                thump_up.foreground.alpha = 255
            }
            LikeType.LIKE -> {
                thump_up.foreground.alpha = 255
                thumb_down.foreground.alpha = 128
            }
            LikeType.DISLIKE -> {
                thump_up.foreground.alpha = 128
                thumb_down.foreground.alpha = 255
            }
        }
    }

    private fun showImage() {
        val builder = Dialog(this,R.style.MyDialog)
        builder.requestWindowFeature(Window.FEATURE_NO_TITLE)
        builder.window!!.setBackgroundDrawable(
        ColorDrawable(Color.TRANSPARENT)
        )
        builder.window!!.setBackgroundDrawableResource(R.color.colorAccent)
        builder.setOnDismissListener{builder.dismiss()}
        builder.setCancelable(true)

        val imageView = ImageView(this)

        Glide.with(this)
            .load("${Global.photoBaseDomain}${event.image}.jpg")
            .into(imageView)

        imageView.setOnClickListener { builder.dismiss() }

        builder.addContentView(imageView, RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT))
        builder.show()
}


    //REPORT

    private fun showReportDialog() {
        val dialog = Dialog(this)
        dialog .requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog .setCancelable(false)
        dialog .setContentView(R.layout.dialog_report)



        val categoryContainer = dialog.findViewById(R.id.reportCategory_in) as TextInputEditText
        val descriptionContainer = dialog.findViewById(R.id.desc2) as TextInputEditText

        val yesBtn = dialog .findViewById(R.id.report_ok) as Button
        val noBtn = dialog .findViewById(R.id.report_cancel) as Button


        categoryContainer.setOnClickListener {
            onChooseReportCategoryClick(dialog)
        }


        noBtn.setOnClickListener {
            dialog .dismiss()
        }

        yesBtn.setOnClickListener {
            val cat = categoryContainer.text.toString()
            val desc = descriptionContainer.text.toString()
            validateCategory(dialog)
            validateDescription(dialog)

            if(validateCategory(dialog) && validateDescription(dialog)){
                val uuid = Global.getInstance()!!.reportHashMap[cat]!!
                createReport(Report(description = desc,category = uuid, event = event))
                dialog .dismiss()
            }
        }
        dialog .show()

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
                    Global.getInstance()!!.reportHashMap[report.title()] = report.uuid().toString()
                }
                println("REPORT CATEGORIES " + response.data().toString())
            }

            override fun onFailure(e: ApolloException) {

                HttpErrorHandler.handle(500)
            }

        })
    }

    private fun onChooseReportCategoryClick(dialog: Dialog) {
        var choose = 0
        val singleChoiceItems :Array<String> = inst!!.reportList.toTypedArray()

        val itemSelected = choose
        val tmp = AlertDialog.Builder(this, R.style.CustomDialogTheme)
            .setTitle(this.getString(R.string.add_dialog_title))
            .setSingleChoiceItems(singleChoiceItems, itemSelected) {
                    _, selectedIndex ->
                choose = selectedIndex
            }
            .setPositiveButton(this.getString(R.string.add_dialog_positive)) {
                    _, _ ->
                val categoryContainer = dialog.findViewById(R.id.reportCategory_in) as TextInputEditText
                categoryContainer.setText(singleChoiceItems[choose])

            }
            .setNegativeButton(this.getString(R.string.add_dialog_negative)) {
                    _, _ ->
            }
            .setOnDismissListener {

            }
            .show()


        val but = tmp.getButton(DialogInterface.BUTTON_POSITIVE)
        but.setTextColor(ContextCompat.getColor(this,R.color.colorPrimary))
        val but2 = tmp.getButton(DialogInterface.BUTTON_NEGATIVE)
        but2.setTextColor(ContextCompat.getColor(this,R.color.colorPrimary))
    }

    private fun validateCategory(dialog: Dialog): Boolean {
        val categoryContainer = dialog.findViewById(R.id.reportCategory_in) as TextInputEditText
        val categoryContainerLayout = dialog.findViewById(R.id.reportCategory) as TextInputLayout

        return if(categoryContainer.text.toString() != ""){
            categoryContainerLayout.error = null
            true
        } else{
            categoryContainerLayout.error = "Wybierz Kategorię"
            false
        }
    }

    private fun validateDescription(dialog: Dialog): Boolean {
        val descriptionContainer = dialog.findViewById(R.id.desc2) as TextInputEditText
        val descriptionContainerLayout = dialog.findViewById(R.id.lay) as TextInputLayout

        return if(descriptionContainer.text.toString() != ""){
            descriptionContainerLayout.error = null
            true
        } else{
            descriptionContainerLayout.error = "Wybierz Kategorię"
            false
        }
    }

}

