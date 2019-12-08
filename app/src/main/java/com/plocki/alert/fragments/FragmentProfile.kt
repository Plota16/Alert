package com.plocki.alert.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.facebook.internal.LoginAuthorizationType
import com.plocki.alert.API.ApolloInstance
import com.plocki.alert.API.modules.FetchEventsHandler
import com.plocki.alert.MyApplication
import com.plocki.alert.R
import com.plocki.alert.activities.LoginPanel
import com.plocki.alert.activities.MainActivity
import com.plocki.alert.models.Global
import com.plocki.alert.utils.Store
import kotlinx.android.synthetic.main.fragment_map.*
import kotlinx.android.synthetic.main.fragment_profile.*
import org.jetbrains.anko.coroutines.experimental.asReference

class FragmentProfile : Fragment() {

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        username.text = Global.getInstance()!!.username

        siqn_out_button.setOnClickListener{
            val sharedstore = Store(MyApplication.getAppContext())
            sharedstore.removeValue("token")
            sharedstore.removeValue("iv")
            Global.getInstance()!!.logged = false
            Global.getInstance()!!.token = ""
//            ApolloInstance.token = ""
        }
    }
}
