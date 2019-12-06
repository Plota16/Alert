package com.plocki.alert.fragments

import android.opengl.Visibility
import androidx.fragment.app.Fragment
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Contacts
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.plocki.alert.recyclerAdapters.ListAdapter
import com.plocki.alert.R
import com.plocki.alert.models.Event
import com.plocki.alert.models.EventMethods
import com.plocki.alert.models.Global
import kotlinx.android.synthetic.main.fragment_list.*
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import okhttp3.internal.wait


class FragmentList : Fragment(){

    val inst = Global.getInstance()



    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        newEventsList.setOnClickListener{
            updateList()
            Global.getInstance()!!.changed = false
            newEventsList.visibility = View.GONE
        }


        GlobalScope.launch(context = Main) {
            while (true){
                if(Global.getInstance()!!.changed){
                    newEventsList.visibility = View.VISIBLE
                }
                else{
                    newEventsList.visibility = View.GONE
                }
                delay(2000)
            }
        }


        updateList()

    }



    override fun onResume() {
        super.onResume()
        updateList()
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if(!hidden){
            updateList()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId

        if (id == R.id.action_refresh) {

            updateList()

        }

        return super.onOptionsItemSelected(item)
    }

    private fun updateList(){

        val filteredList = ArrayList<Event>()
        for (event in inst!!.list) {
            val index = inst.CategoryList.indexOf(EventMethods.getCategory(event.category))
            if (inst.FilterList[index]) {
                if (EventMethods.calcDistance(event.coords) < EventMethods.getMaxDistance(inst.filterdDistnance) || EventMethods.getMaxDistance(
                        inst.filterdDistnance
                    ) == 0
                ) {
                    filteredList.add(event)
                }
            }
        }
        val sortedList = filteredList.sortedBy { EventMethods.calcDistance(it.coords) }
        // RecyclerView node initialized here
        recycler.apply {
            // set a LinearLayoutManager to handle Android
            // RecyclerView behavior
            layoutManager = LinearLayoutManager(activity)
            // set the custom adapter to the RecyclerView
            adapter = ListAdapter(sortedList, activity!!, context)

        }
    }



}