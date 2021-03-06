package com.plocki.alert.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.plocki.alert.R
import com.plocki.alert.activities.Details
import com.plocki.alert.models.Event
import com.plocki.alert.utils.EventMethods
import com.plocki.alert.models.Global
import java.math.BigDecimal
import java.math.RoundingMode
import java.util.*
import kotlin.math.roundToInt

class EventViewHolder(inflater: LayoutInflater, parent: ViewGroup, act: FragmentActivity, cont : Context) :
    RecyclerView.ViewHolder(inflater.inflate(R.layout.event, parent, false)),View.OnClickListener {

    private var inst = Global.getInstance()
    private var con : Context? = null
    private var ac : FragmentActivity? = null
    private var eventTitle: TextView? = null
    private var eventCategory: TextView? = null
    private var eventDescription: TextView? = null
    private var imageView : ImageView? = null

    init {
        this.con = cont
        this.ac = act
    }


    override fun onClick(view: View?) {
        val intent = Intent(con, Details::class.java)
        intent.putExtra("pos", layoutPosition.toString())
        con!!.startActivity(intent)
    }





    init {
        eventTitle = itemView.findViewById(R.id.list_title)
        eventCategory = itemView.findViewById(R.id.list_category)
        eventDescription = itemView.findViewById(R.id.list_distance)
        imageView = itemView.findViewById(R.id.imageView)
        itemView.setOnClickListener(this)
    }




    fun bind(event: Event) {
        inst!!.listHashMap[layoutPosition.toString()] = event

        val res: String
        val dist = EventMethods.calcDistance(event.coordinates)
        if(dist < 1000){
            var dintInDouble = dist.toDouble()
            dintInDouble /= 10
            var distInInt = dintInDouble.roundToInt()
            distInInt *= 10
            res = "$distInInt m"
        }
        else{
            var dintInDouble = dist.toDouble()
            dintInDouble /= 1000
            val distInInt = BigDecimal(dintInDouble).setScale(1, RoundingMode.HALF_EVEN)
            res = "$distInInt km"
        }

        val categoryContainer = event.category.title.toUpperCase(Locale.ENGLISH)  + " ($res)"
        eventCategory?.text = categoryContainer
        eventTitle?.text = event.title

        if(event.description != null){
            eventDescription?.text = event.description
        }

        if (con != null) {
            Glide.with(con)
                .load("${Global.photoBaseDomain}${event.image}.jpg")
                .centerCrop()
                .placeholder(R.drawable.placeholder)
                .override(120,90)
                .into(imageView)
        }

    }

}