package com.plocki.alert.fragments


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.plocki.alert.R
import com.plocki.alert.models.Global
import com.plocki.alert.utils.AppLauncher
import kotlinx.android.synthetic.main.fragment_profile.*

class FragmentProfile : Fragment() {

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        username.text = Global.getInstance()!!.userName

        siqn_out_button.setOnClickListener{
            AppLauncher.logOut()
        }
    }
}
