package com.plocki.alert.recyclerAdapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.plocki.alert.EventViewHolder
import com.plocki.alert.models.Event

class ListAdapter(private val list: List<Event>, act : FragmentActivity, cont : Context)
    : RecyclerView.Adapter<EventViewHolder>() {

    var ac: FragmentActivity? = null
    var con: Context? = null
    init {
        this.con = cont
        this.ac = act
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return EventViewHolder(inflater, parent, ac!!, con!!)
    }

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        val movie: Event = list[position]
        holder.bind(movie)
    }

    override fun getItemCount(): Int = list.size

}