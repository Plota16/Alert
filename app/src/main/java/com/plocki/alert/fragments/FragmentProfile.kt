package com.plocki.alert.fragments


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.plocki.alert.R
import com.plocki.alert.models.Global
import com.plocki.alert.utils.AppLauncher
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
            println(Global.getInstance()!!.userToken)
            updateProfile()
        }
        siqn_out_button.setOnClickListener{
            AppLauncher.logOut()
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
}
