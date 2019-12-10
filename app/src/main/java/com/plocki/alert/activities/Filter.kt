package com.plocki.alert.activities

import android.app.AlertDialog
import android.content.DialogInterface
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.transition.TransitionManager
import android.view.View
import com.google.android.material.chip.Chip
import com.plocki.alert.R
import com.plocki.alert.models.Global
import kotlinx.android.synthetic.main.activity_filter.*

class Filter : AppCompatActivity() {

    private val inst = Global.getInstance()
    private var chipList = HashMap<Int, Chip>()
    private var distance = 0
    lateinit var checkedList : BooleanArray

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_filter)

        supportActionBar!!.title = this.getString(R.string.filter_title)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        filter_distance.keyListener = null
        filter_category.keyListener = null

        distance = inst!!.distanceList.indexOf(inst.currentDistanceFilter)
        checkedList = inst.filterList
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
                    dialogInterface, selectedIndex -> distance = selectedIndex}
            .setPositiveButton(this.getString(R.string.add_dialog_positive)) {
                    dialog, which ->
                filter_distance.setText(inst.distanceList[distance])  }
            .setNegativeButton(this.getString(R.string.add_dialog_negative), null)
            .show()


        val but = tmp.getButton(DialogInterface.BUTTON_POSITIVE)
        but.setTextColor(Color.parseColor("#6200EE"))
        val but2 = tmp.getButton(DialogInterface.BUTTON_NEGATIVE)
        but2.setTextColor(Color.parseColor("#6200EE"))
    }

    fun onCategoryMark() {

        val tmp = AlertDialog.Builder(this, R.style.CustomDialogTheme)
            .setTitle(this.getString(R.string.add_dialog_title))
            .setMultiChoiceItems(inst!!.categoryList, checkedList) {
                dialog : DialogInterface, which : Int, isChecked : Boolean ->

                checkedList[which] = isChecked
            }

            .setPositiveButton(this.getString(R.string.add_dialog_positive)) {
                    dialog, which ->

               for(i in checkedList.indices)
               {
                   chipGroup.removeView(chipList.get(1000+i))
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
            if(inst.filterList[i]){
                val chip = Chip(this)
                val id = 1000 + i

                chip.text = inst.categoryList[i]
                chip.isCloseIconVisible = true

                chip.setOnCloseIconClickListener{
                    TransitionManager.beginDelayedTransition(chipGroup)
                    chipGroup.removeView(chip)
                    checkedList[i] = false
                }
                chipList.put(id,chip)
                chipGroup.addView(chip)
            }
        }
    }

    fun confirm(v: View){
        inst!!.currentDistanceFilter = inst.distanceList[distance]
        inst.filterList = checkedList
        finish()
    }

    fun reset(v: View){
        distance = 0
        filter_distance.setText("")
        for(i in inst!!.filterList.indices){
            checkedList[i] = true
        }
        for(i in checkedList.indices)
        {
            chipGroup.removeView(chipList[1000+i])
        }
        generateChips()
    }
}
