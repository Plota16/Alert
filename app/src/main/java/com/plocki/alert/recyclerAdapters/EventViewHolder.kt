package com.plocki.alert

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
import com.plocki.alert.activities.Details
import com.plocki.alert.models.Event
import com.plocki.alert.models.EventMethods
import com.plocki.alert.models.Global
import java.math.BigDecimal
import java.math.RoundingMode
import kotlin.math.roundToInt

class EventViewHolder(inflater: LayoutInflater, parent: ViewGroup, act: FragmentActivity, cont : Context) :
RecyclerView.ViewHolder(inflater.inflate(R.layout.event, parent, false)),View.OnClickListener {

    private var inst = Global.getInstance()
    private var con : Context? = null
    private var ac : FragmentActivity? = null
    private var eventTitle: TextView? = null
    private var eventCategory: TextView? = null
    private var eventDistance: TextView? = null
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
        eventDistance = itemView.findViewById(R.id.list_distance)
        imageView = itemView.findViewById(R.id.imageView)
        itemView.setOnClickListener(this)
    }




    fun bind(event: Event) {
        inst!!.listHashMap.put(layoutPosition.toString(),event)
        eventTitle?.text = event.title
        eventCategory?.text = event.category
        var res = ""
        val dist = EventMethods.calcDistance(event.coords)
        if(dist < 1000){
            var tmp = dist.toDouble()
            tmp /= 10
            var tmp2 = tmp.roundToInt()
            tmp2 *= 10
            res = "$tmp2 m"
        }
        else{
            var tmp = dist.toDouble()
            tmp /= 1000
            val tmp2 = BigDecimal(tmp).setScale(1, RoundingMode.HALF_EVEN)
            res = "$tmp2 km"
        }
        eventDistance?.text = res

        if (con != null) {
            Glide.with(con)
                .load(event.image)
                .placeholder(R.drawable.placeholder)
                .override(120,90)
                .into(imageView)
        }

    }

}