package com.plocki.alert.activities

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.DialogInterface
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.transition.TransitionManager
import androidx.core.view.marginBottom
import com.google.android.material.chip.Chip
import com.plocki.alert.R
import com.plocki.alert.models.Global
import com.plocki.alert.utils.Store
import kotlinx.android.synthetic.main.activity_filter.*

class Filter : AppCompatActivity() {

    private val inst = Global.getInstance()
    @SuppressLint("UseSparseArrays")
    private var chipList = HashMap<Int, Chip>()
    private var distance = 0
    private var filterList = booleanArrayOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_filter)

        resetButton.setOnClickListener {
            reset()
        }

        confirmButton.setOnClickListener {
            confirm()
        }


        supportActionBar!!.title = this.getString(R.string.filter_title)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        filter_distance.keyListener = null
        filter_category.keyListener = null


        val boolContainer = ArrayList<Boolean>()
        distance = inst!!.distanceList.indexOf(inst.currentDistanceFilter)
        for (category in Global.getInstance()!!.categoryList){
            boolContainer.add(Global.getInstance()!!.filterHashMap[category]!!)
        }
        filterList = boolContainer.toBooleanArray()
        if(distance != 0){
            filter_distance.setText(inst.distanceList[distance])
        }

        filter_distance.setOnClickListener{
            onDistanceChoose()
        }

        filter_category.setOnClickListener{
            onCategoryMark()
        }

        generateChips()

    }


    private fun onDistanceChoose() {

        val itemSelected = distance
        val tmp = AlertDialog.Builder(this, R.style.CustomDialogTheme)
            .setTitle(this.getString(R.string.filter_dostance_title))
            .setSingleChoiceItems(inst!!.distanceList, itemSelected) {
                    _, selectedIndex -> distance = selectedIndex}
            .setPositiveButton(this.getString(R.string.add_dialog_positive)) {
                    _, _ ->
                filter_distance.setText(inst.distanceList[distance])  }
            .setNegativeButton(this.getString(R.string.add_dialog_negative), null)
            .show()


        val but = tmp.getButton(DialogInterface.BUTTON_POSITIVE)
        but.setTextColor(Color.parseColor("#6200EE"))
        val but2 = tmp.getButton(DialogInterface.BUTTON_NEGATIVE)
        but2.setTextColor(Color.parseColor("#6200EE"))
    }

    private fun onCategoryMark() {

        val multiChoiceList = inst!!.categoryList.toTypedArray()
        val tmp = AlertDialog.Builder(this, R.style.CustomDialogTheme)
            .setTitle(this.getString(R.string.add_dialog_title))
            .setMultiChoiceItems(multiChoiceList, filterList) {
                    _: DialogInterface, which : Int, isChecked : Boolean ->
                filterList[which] = isChecked
            }

            .setPositiveButton(this.getString(R.string.add_dialog_positive)) {
                    _, _ ->

               for(i in filterList.indices)
               {
                   chipGroup.removeView(chipList[1000+i])
               }
                generateChips()
            }
            .setNegativeButton(this.getString(R.string.add_dialog_negative), null)
            .show()


        val but = tmp.getButton(DialogInterface.BUTTON_POSITIVE)
        but.setTextColor(Color.parseColor("#6200EE"))
        val but2 = tmp.getButton(DialogInterface.BUTTON_NEGATIVE)
        but2.setTextColor(Color.parseColor("#6200EE"))
    }

    private fun generateChips(){
        for(i in inst!!.categoryList.indices){
            if(filterList[i]){
                val chip = Chip(this)
                val id = 1000 + i

                chip.text = inst.categoryList[i]
                chip.isCloseIconVisible = true

                chip.setOnCloseIconClickListener{
                    TransitionManager.beginDelayedTransition(chipGroup)
                    chipGroup.removeView(chip)
                    filterList[i] = false
                }
                chipList[id] = chip
                chipGroup.addView(chip)
            }
        }
    }

    private fun confirm(){
        val store = Store(this)
        inst!!.currentDistanceFilter = inst.distanceList[distance]
        store.storeDistance(inst.distanceList[distance])
        for(i in inst.categoryList){
            inst.filterHashMap[i] = filterList[inst.categoryList.indexOf(i)]
        }
        finish()
    }

    private fun reset(){
        distance = 0
        filter_distance.setText("")
        for(i in inst!!.categoryList.indices){
            filterList[i] = true
        }
        for(i in filterList.indices)
        {
            chipGroup.removeView(chipList[1000+i])
        }
        generateChips()
    }
}
