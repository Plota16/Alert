package com.plocki.alert.services

import android.app.Activity
import android.widget.Toast
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.plocki.alert.activities.LoginPanel
import com.plocki.alert.models.ProviderType
import com.plocki.alert.models.ProviderUser
import com.plocki.alert.utils.AppLauncher
import java.util.logging.Logger

class GoogleService(activity: Activity) {
    private var acct: GoogleSignInAccount?
    var mGoogleSignInClient: GoogleSignInClient
    private val logger = Logger.getLogger(LoginPanel::class.java.toString())
    private val mActivity: Activity = activity

    init {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("346357530109-vt499lq6b5e9pdvittg75q44js5s0v0g.apps.googleusercontent.com")
            .requestEmail()
            .build()
        mGoogleSignInClient = GoogleSignIn.getClient(mActivity, gso)
        acct = GoogleSignIn.getLastSignedInAccount(mActivity)
    }


    fun signOut() {
        val task = mGoogleSignInClient.signOut()
        task.addOnCompleteListener {
            task.addOnCanceledListener {
                Toast.makeText(mActivity, "Cancel wylogowany", Toast.LENGTH_LONG).show()
            }
            task.addOnSuccessListener {
                Toast.makeText(mActivity, "wylogowany", Toast.LENGTH_LONG).show()

            }
            task.addOnFailureListener {
                Toast.makeText(mActivity, "Fail", Toast.LENGTH_LONG).show()

            }
        }
    }

    fun handleGoogleSignInResult(task: Task<GoogleSignInAccount>) {
        try {
            task.addOnCompleteListener {
                logger.info("COMPLETE")
                task.addOnCanceledListener {
                    //TODO czy coś z tym robić
//                    Toast.makeText(mActivity, "Nie można połączyć się przy pomocy Google", Toast.LENGTH_LONG).show()

                }
                task.addOnSuccessListener {
                    val account = task.result
                    val idToken = account!!.idToken
                    println("TOKEN $idToken")

                    logger.info(account.email)
                    logger.info(account.displayName)
                    logger.info(account.id)
                    logger.info(account.account.toString())
                    logger.info(account.isExpired.toString())
                    val providerUser = ProviderUser(ProviderType.GOOGLE, idToken.toString())
                    AppLauncher.launchApp(mActivity, providerUser)
                }
                task.addOnFailureListener {
                    println("ERRR" + it.message)
                    Toast.makeText(mActivity, "Nie można połączyć się przy pomocy Google", Toast.LENGTH_LONG).show()
                }
            }
        } catch (e: ApiException) {
            println("Błąd")
        }
    }
}


