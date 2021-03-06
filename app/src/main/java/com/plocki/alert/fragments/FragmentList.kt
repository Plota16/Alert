package com.plocki.alert.fragments

import androidx.fragment.app.Fragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.plocki.alert.MyApplication
import com.plocki.alert.adapters.ListAdapter
import com.plocki.alert.R
import com.plocki.alert.models.Event
import com.plocki.alert.utils.EventMethods
import com.plocki.alert.models.Global
import kotlinx.android.synthetic.main.fragment_list.*
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class FragmentList : Fragment(){

    private val inst = Global.getInstance()
    private var isFilteringPossible = false


    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        GlobalScope.launch(context = Main) {
            while (true){
                    if(Global.getInstance()!!.isDataChanged){
                        Toast.makeText(MyApplication.getAppContext(), "Aktualizacja Danych", Toast.LENGTH_LONG).show()
                        updateList()
                        Global.getInstance()!!.isDataChanged = false
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
        if(isFilteringPossible){
            for (event in inst!!.eventList) {
                if (inst.filterHashMap[event.category.title]!!) {
                    if (EventMethods.calcDistance(event.coordinates) < EventMethods.getMaxDistance(inst.currentDistanceFilter) || EventMethods.getMaxDistance(
                            inst.currentDistanceFilter
                        ) == 0
                    ) {
                        filteredList.add(event)
                    }
                }
            }
        }
        else{
            for (event in inst!!.eventList) {
                    if (EventMethods.calcDistance(event.coordinates) < EventMethods.getMaxDistance(inst.currentDistanceFilter) || EventMethods.getMaxDistance(
                            inst.currentDistanceFilter
                        ) == 0
                    ) {
                        filteredList.add(event)
                    }
                }
            isFilteringPossible = true
        }
        val sortedList = filteredList.sortedBy { EventMethods.calcDistance(it.coordinates) }
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