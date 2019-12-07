package com.plocki.alert.utils

import android.content.Context
import android.util.Base64
import com.plocki.alert.ciphers.Decryptor
import com.plocki.alert.ciphers.Encryptor
import android.content.SharedPreferences
import android.preference.PreferenceManager
import com.facebook.FacebookSdk.getApplicationContext
import com.plocki.alert.MyApplication
import java.lang.Exception


class Store(context: Context) {

    private val encryptor = Encryptor()
    val decryptor  = Decryptor()

    private val pref  = PreferenceManager.getDefaultSharedPreferences(context)
    private val editor : SharedPreferences.Editor = pref.edit()


    fun storeValue(name: String,value: String){
        val cipherClass = encryptor.encryptText("ALIAS",value)
        val encodedValueString = Base64.encodeToString(cipherClass.encrypter, Base64.DEFAULT)
        val encodedIVString = Base64.encodeToString(cipherClass.iv, Base64.DEFAULT)
        editor.putString(name,encodedValueString)
        editor.apply()
        editor.putString("iv",encodedIVString)
        editor.apply()
        editor.commit()
    }

    fun retrieveValue(name: String) : String{
        val encodedValue = pref.getString(name,"")
        val encodedIV = pref.getString("iv","")
        val encodedValueByteArray = Base64.decode(encodedValue, Base64.DEFAULT)
        val encodedIVByteArray = Base64.decode(encodedIV, Base64.DEFAULT)
            val result = decryptor.decryptData("ALIAS",encodedValueByteArray, encodedIVByteArray)
        return result
    }

    fun removeValue(name: String){
        editor.remove(name)
        editor.commit()
    }
}