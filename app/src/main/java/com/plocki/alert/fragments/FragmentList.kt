package com.plocki.alert.fragments

import androidx.fragment.app.Fragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.plocki.alert.recyclerAdapters.ListAdapter
import com.plocki.alert.R
import com.plocki.alert.models.Global
import kotlinx.android.synthetic.main.list_fragment.*



class FragmentList : Fragment(){

    val glob = Global.getInstance()



    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        return inflater.inflate(R.layout.list_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


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