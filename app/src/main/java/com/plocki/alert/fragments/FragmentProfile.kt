package com.plocki.alert.fragments


import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.apollographql.apollo.ApolloCall
import com.apollographql.apollo.api.Response
import com.apollographql.apollo.exception.ApolloException
import com.google.gson.GsonBuilder
import com.google.gson.JsonSyntaxException
import com.plocki.alert.API.modules.UserApi
import com.plocki.alert.DeleteUserMutation
import com.plocki.alert.R
import com.plocki.alert.models.Global
import com.plocki.alert.utils.AppLauncher
import com.plocki.alert.utils.HttpErrorHandler
import com.plocki.alert.utils.Store
import kotlinx.android.synthetic.main.fragment_profile.*

class FragmentProfile : Fragment() {

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        if(Global.getInstance()!!.userToken != "") {
            updateProfile()
        }
        siqn_out_button.setOnClickListener{
            AppLauncher.logOut(true)
        }

        delete_user_button.setOnClickListener{
            deleteUser()
        }
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        updateProfile()
    }

    private fun updateProfile(){
        val store = Store(context!!)
        profile_provider.text = store.retrieveProvider()

        when {
            Global.getInstance()!!.userData.reputation > 0 -> {
                val likeContainer = "+" + Global.getInstance()!!.userData.reputation.toString()
                profile_reputacion_number.text = likeContainer
                profile_reputacion_number.setTextColor(ContextCompat.getColor(context!!, R.color.green))
            }
            Global.getInstance()!!.userData.reputation < 0 -> {
                val likeContainer = Global.getInstance()!!.userData.reputation.toString()
                profile_reputacion_number.text= likeContainer
                profile_reputacion_number.setTextColor(ContextCompat.getColor(context!!, R.color.red))

            }
            else -> profile_reputacion_number.text = Global.getInstance()!!.userData.reputation.toString()
        }


        profile_statistic_add_num.text = Global.getInstance()!!.userData.createdEvents.toString()
        profile_statistic_rate_num.text = Global.getInstance()!!.userData.likesGiven.toString()
        profile_statistic_report_num.text = Global.getInstance()!!.userData.reportsReported.toString()

    }

    fun deleteUser() {
        val deleteUser = UserApi.deleteUser(
            object : ApolloCall.Callback<DeleteUserMutation.Data>() {
                override fun onFailure(e: ApolloException) {
                }

                override fun onResponse(response: Response<DeleteUserMutation.Data>) {
                    if (response.hasErrors()) {
                        Log.e("ERROR ", response.errors()[0].customAttributes()["statusCode"].toString())
                        val gson = GsonBuilder().create()
                        try {
                            val errorMap =
                                gson.fromJson(response.errors()[0].message(), Map::class.java)
                            HttpErrorHandler.handle(errorMap["statusCode"].toString().toFloat().toInt())
                        } catch (e: JsonSyntaxException) {

                            HttpErrorHandler.handle(500)
                            Log.e("ERROR ","Błąd bazy danych")

                        }
                        return
                    }
                    AppLauncher.logOut(true)

                }
            }
        )
    }
}
