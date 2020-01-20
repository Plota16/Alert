package com.plocki.alert.utils

import android.content.Context
import android.util.Base64
import com.plocki.alert.ciphers.Decryptor
import com.plocki.alert.ciphers.Encryptor
import android.content.SharedPreferences
import android.preference.PreferenceManager


class Store(context: Context) {

    private val encryptor = Encryptor()
    private val decryptor  = Decryptor()

    private val pref  = PreferenceManager.getDefaultSharedPreferences(context)
    private val editor : SharedPreferences.Editor = pref.edit()



    fun storeProvider(value: String){
        editor.putString("provider",value)
        editor.apply()
        editor.commit()
    }

    fun retrieveProvider() : String?{
        return pref.getString("provider","")
    }

    fun removeProvider() {
        editor.remove("provider")
        editor.commit()
    }

    fun storeToken(value: String){
        val cipherClass = encryptor.encryptText("ALIAS",value)
        val encodedValueString = Base64.encodeToString(cipherClass.encrypter, Base64.DEFAULT)
        val encodedIVString = Base64.encodeToString(cipherClass.iv, Base64.DEFAULT)
        editor.putString("userToken",encodedValueString)
        editor.apply()
        editor.putString("iv",encodedIVString)
        editor.apply()
        editor.commit()
    }

    fun retrieveToken() : String{
        val encodedValue = pref.getString("userToken","")
        val encodedIV = pref.getString("iv","")
        val encodedValueByteArray = Base64.decode(encodedValue, Base64.DEFAULT)
        val encodedIVByteArray = Base64.decode(encodedIV, Base64.DEFAULT)
            val result = decryptor.decryptData("ALIAS",encodedValueByteArray, encodedIVByteArray)
        return result
    }

    fun removeToken(){
        editor.remove("userToken")
        editor.remove("iv")
        editor.commit()
    }
}