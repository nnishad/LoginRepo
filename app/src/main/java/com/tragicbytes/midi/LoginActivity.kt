package com.tragicbytes.midi

import android.app.AlertDialog
import android.content.ActivityNotFoundException
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import kotlinx.android.synthetic.main.activity_main.*

class LoginActivity : FirebaseConfig() {
    private lateinit var mFirebaseRemoteConfig: FirebaseRemoteConfig
    private var VersionCode = "versionCode"
    private lateinit var mAuth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mFirebaseRemoteConfig = getRemoteConfigValues()
        setRemoteConfigValues()
        loginbtn.setOnClickListener {
            //region LoginButtonFunctionality
            try {

                val email = username.text.toString().trim()
                val passwords = password.text.toString().trim()

                if (email.isEmpty()) {
                    username.error = "Email Required"
                    username.requestFocus()
                    return@setOnClickListener
                }

                if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    username.error = "Invalid Email Address"
                    username.requestFocus()
                    return@setOnClickListener
                }

                if (passwords.isEmpty() || passwords.length < 6) {
                    password.error = "6 char password required"
                    password.requestFocus()
                    return@setOnClickListener
                }
                /*username.isEnabled=false
                password.isEnabled=false*/
                loginUser(email, passwords)

            } catch (e: Exception) {
                loginbtn.error = e.message
            }

        }

        register.setOnClickListener { startActivity(Intent(this,MobileNumber::class.java)) }
    }

    //region Firebase Config Method 2
    private fun setRemoteConfigValues() {
        //region Fetching Values
        val remoteCodeVersion = mFirebaseRemoteConfig.getLong(VersionCode)
        val AlertTitle = mFirebaseRemoteConfig.getString("Alert_Title")
        val AlertMessage = mFirebaseRemoteConfig.getString("Alert_Message")
        val Alert_Ok_btn = mFirebaseRemoteConfig.getString("Alert_Ok_Btn")
        val Alert_No_btn = mFirebaseRemoteConfig.getString("Alert_No_Btn")

        //endregion

        //region App Update Dialog Box
        if (remoteCodeVersion > 0) {
            val versionCode = BuildConfig.VERSION_CODE
            if (remoteCodeVersion > versionCode) {
                val dialogBuilder = AlertDialog.Builder(this)


                // set message of alert dialog
                dialogBuilder.setMessage(AlertMessage)
                    // if the dialog is cancelable
                    .setCancelable(false)
                    // positive button text and action
                    .setPositiveButton(Alert_Ok_btn, DialogInterface.OnClickListener { _, _ ->
                        val uri = Uri.parse("market://details?id=" + this@LoginActivity.packageName)
                        val goToMarket = Intent(Intent.ACTION_VIEW, uri)
                        // To count with Play market backstack, After pressing back button,
                        // to taken back to our application, we need to add following flags to intent.
                        goToMarket.addFlags(
                            Intent.FLAG_ACTIVITY_NO_HISTORY or
                                    Intent.FLAG_ACTIVITY_NEW_DOCUMENT or
                                    Intent.FLAG_ACTIVITY_MULTIPLE_TASK
                        )
                        try {
                            startActivity(goToMarket)
                        } catch (e: ActivityNotFoundException) {
                            startActivity(
                                Intent(
                                    Intent.ACTION_VIEW,
                                    Uri.parse("http://play.google.com/store/apps/details?id=" + this@LoginActivity.packageName)
                                )
                            )
                        }
                    })
                    // negative button text and action
                    .setNegativeButton(Alert_No_btn, // do something when the button is clicked
                        DialogInterface.OnClickListener { _, _ ->
                            finishAffinity()
                        })


                // create dialog box
                val alert = dialogBuilder.create()
                // set title for alert dialog box
                alert.setTitle(AlertTitle)
                // show alert dialog
                alert.show()
            }
            //  main_layout!!.setBackgroundColor(Color.parseColor(remoteValueText))
        }
        //endregion

    }
    //endregion

    //region Firebase Config Method 3
    override fun onStart() {

        super.onStart()
        mFirebaseRemoteConfig = getRemoteConfigValues()
        //region Startup Notification Firebase Config
        val remoteCodeVersion = mFirebaseRemoteConfig.getLong(VersionCode)
        val versionCode = BuildConfig.VERSION_CODE

        if (remoteCodeVersion > versionCode) {
            getRemoteConfigValues()
        }
        //endregion
    }
    //endregion

    //region Back Press
    override fun onBackPressed() {
        finishAffinity()
        finish()
    }
    //endregion

    //region OnResume
    override fun onResume() {

        mFirebaseRemoteConfig = getRemoteConfigValues()
        //region Startup Notification Firebase Config
        val remoteCodeVersion = mFirebaseRemoteConfig.getLong(VersionCode)
        val versionCode = BuildConfig.VERSION_CODE

        if (remoteCodeVersion > versionCode) {
            getRemoteConfigValues()
        }
        super.onResume()
    }
    //endregion

    private fun loginUser(email: String, password: String) {

        loading.visibility = View.VISIBLE
         mAuth.signInWithEmailAndPassword(email, password)
             .addOnCompleteListener(this) {

                     task ->

                 if (task.isSuccessful) {

                     // login()
                     //region LoginMethod
                     val currentUser = FirebaseAuth.getInstance().currentUser
//                     currentUser?.let { user ->
//                         val rootRef = FirebaseDatabase.getInstance().reference
//                         val userNameRef = rootRef.child("users").orderByChild("email").equalTo(user.email)
//                         val eventListener = object : ValueEventListener {
//                             override fun onDataChange(dataSnapshot: DataSnapshot) {
//                                 for (e in dataSnapshot.children) {
//                                     val employee = e.getValue(Data_info::class.java)
//                                     if (employee != null) {
//                                         val u_type = employee.user_type
//                                         loading.visibility = View.GONE
//                                         if (u_type == "S") startActivity(
//                                             Intent(
//                                                 this@LoginActivity,
//                                                 UserHomeV2::class.java
//                                             )
//                                         )
//                                         else if (u_type == "M") startActivity(
//                                             Intent(
//                                                 this@LoginActivity,
//                                                 Mentorhomev2::class.java
//                                             )
//                                         )
//
//                                     }
//                                 }
//                             }
//
//                             override fun onCancelled(databaseError: DatabaseError) {
//                             }
//                         }
//                         userNameRef.addListenerForSingleValueEvent(eventListener)
//
//                     }
                     //endregion


                 } else if (task.isCanceled) {
                     loading.visibility = View.GONE
                     task.exception?.message?.let {

                         Toast.makeText(this, "Login Failed", Toast.LENGTH_SHORT).show()
                     }
                 } else {
                     loading.visibility = View.GONE
                     task.exception?.message?.let {
                        // toast(it)
                         //Toast.makeText( this, "Login Failed", Toast.LENGTH_SHORT ).show();
                     }
                 }
             }
    }


}
