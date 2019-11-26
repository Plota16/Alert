package com.plocki.alert.activities


import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import com.plocki.alert.R

class Splash : Activity() {

    /** Duration of wait  */
    private val SPLASH_DISPLAY_LENGTH = 1000

    /** Called when the activity is first created.  */
    public override fun onCreate(icicle: Bundle?) {
        super.onCreate(icicle)
        setContentView(R.layout.splash)

        /* New Handler to start the Menu-Activity
         * and close this Splash-Screen after some seconds.*/
        Handler().postDelayed({
            /* Create an Intent that will start the Menu-Activity. */
            val mainIntent = Intent(this@Splash, LoginPanel::class.java)
            this@Splash.startActivity(mainIntent)
            this@Splash.finish()
        }, SPLASH_DISPLAY_LENGTH.toLong())
    }
}