package com.plocki.alert.fragments

import androidx.fragment.app.Fragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.plocki.alert.recyclerAdapters.ListAdapter
import com.plocki.alert.R
import com.plocki.alert.models.EventMethods
import com.plocki.alert.models.Global
import kotlinx.android.synthetic.main.fragment_list.*



class FragmentList : Fragment(){

    val glob = Global.getInstance()



    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        return inflater.inflate(R.layout.fragment_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        glob!!.list = glob!!.list.sortedBy { EventMethods.calcDistance(it.coords) }

        // RecyclerView node initialized here
        recycler.apply {
            // set a LinearLayoutManager to handle Android
            // RecyclerView behavior
            layoutManager = LinearLayoutManager(activity)
            // set the custom adapter to the RecyclerView
            adapter = ListAdapter(glob!!.list, activity!!, context)
        }



    }
}