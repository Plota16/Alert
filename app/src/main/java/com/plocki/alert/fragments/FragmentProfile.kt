package com.plocki.alert.fragments


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.apollographql.apollo.ApolloCall
import com.apollographql.apollo.api.Response
import com.apollographql.apollo.exception.ApolloException
import com.google.gson.GsonBuilder
import com.plocki.alert.API.modules.ReportCategoriesApi
import com.plocki.alert.API.modules.StatsApi
import com.plocki.alert.AllReportCategoriesQuery
import com.plocki.alert.MyStatsQuery
import com.plocki.alert.R
import com.plocki.alert.models.Global
import com.plocki.alert.utils.AppLauncher
import com.plocki.alert.utils.HttpErrorHandler
import kotlinx.android.synthetic.main.fragment_profile.*

class FragmentProfile : Fragment() {

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        if(Global.getInstance()!!.userToken != "") {
            println(Global.getInstance()!!.userToken)
            StatsApi.fetchStats(object : ApolloCall.Callback<MyStatsQuery.Data>(){
                override fun onResponse(response: Response<MyStatsQuery.Data>) {
                    if (response.hasErrors()) {
                        val gson = GsonBuilder().create()
                        val errorMap = gson.fromJson(response.errors()[0].message(), Map::class.java)
                        HttpErrorHandler.handle(errorMap["statusCode"].toString().toFloat().toInt())
                        return
                    }
                    println("PROFILE FRAAGMENT STATS " + response.data().toString())
                }
                override fun onFailure(e: ApolloException) {

                    HttpErrorHandler.handle(500)
                }

            })

        }
        siqn_out_button.setOnClickListener{
            AppLauncher.logOut()
        }
    }
}
