package com.tragicbytes.midi

import android.graphics.Color
import android.hardware.biometrics.BiometricPrompt
import android.os.Bundle
import android.text.TextUtils
import android.util.Patterns
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import com.chaos.view.PinView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_mobile_number.*
import java.util.concurrent.TimeUnit


class MobileNumber : FirebaseConfig(), View.OnClickListener {
    private lateinit var mAuth: FirebaseAuth
    lateinit var ref: DatabaseReference
    private var storedVerificationId: String? = null
    private lateinit var resendToken: PhoneAuthProvider.ForceResendingToken

    private lateinit var pinView: PinView
    private lateinit var next: Button
    private lateinit var topText: TextView
    private lateinit var textU: TextView
    private lateinit var userName: EditText
    private lateinit var userPhone: EditText
    private lateinit var first: ConstraintLayout
    private lateinit var second: ConstraintLayout
    private lateinit var third: ConstraintLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mobile_number)
        mAuth = FirebaseAuth.getInstance()
        ref = FirebaseDatabase.getInstance().getReference("users")

        //region Elements Typecasting
        topText = findViewById(R.id.topText);
        pinView = findViewById(R.id.pinView);
        next = findViewById(R.id.button);
        userName = findViewById(R.id.username);
        userPhone = findViewById(R.id.userPhone);
        first = findViewById(R.id.first_step);
        second = findViewById(R.id.secondStep);
        third = findViewById(R.id.thirdStep)
        textU = findViewById(R.id.textView_noti);
        first.visibility = View.VISIBLE;
        next.setOnClickListener(this);
        third.setOnClickListener(this)
        third.visibility = View.GONE



    }

    override fun onClick(v: View?) {
//        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        if (next.text == "Let's go!") {
            val name: String = userName.text.toString()
            val phone: String = userPhone.text.toString()
            var number_flag = 0

            if (name.isEmpty()) {

                userName.error = "Name Required"
                userName.requestFocus()
                //  return
            }
            if (phone.isEmpty()) {
                userPhone.error = "Phone Required"
                userPhone.requestFocus()
                //return
            }
            if (!Patterns.PHONE.matcher(phone).matches() || phone.length > 10 || phone.length < 10) {
                userPhone.error = "Valid Phone Number Required"
                userPhone.requestFocus()
                number_flag = 1
                //return@setOnClickListener
            }

            if (!TextUtils.isEmpty(name) && !TextUtils.isEmpty(phone) && number_flag == 0) {
                next.text = "Verify"
                first.visibility = View.GONE
                second.visibility = View.VISIBLE
                third.visibility = View.GONE
                topText.text =
                    "I Still don't trust you.\nTell me something that only two of us know."
                registerUser(
                    phone, name
                )
               // PhoneAuthProvider.getInstance().verifyPhoneNumber(phone,60,TimeUnit.SECONDS,this,callbacks)
            } else {
                /*Toast.makeText(this, "Please enter the details", Toast.LENGTH_SHORT)
                    .show()*/
            }
        } else if (next.text == "Verify") {

            val OTP: String = pinView.text.toString()
            if (OTP == "3456") {
                pinView.setLineColor(Color.GREEN)
                textU.text = "OTP Verified"
                textU.setTextColor(Color.GREEN)
                next.text = "Next"

            } else {
                pinView.setLineColor(Color.RED)
                textU.text = "X Incorrect OTP"
                textU.setTextColor(Color.RED)
            }
        } else if (next.text == "Next") {
            val email: String = userEmail.text.toString()
            val dob: String = userDOB.text.toString()
            topText.text =
                "Now We are friends,\nComplete the last step to create unbreakable bond!"
            second.visibility = View.GONE
            third.visibility = View.VISIBLE
            next.text = "Submit"
            /*if (email.isEmpty()) {

                userEmail.error = "Email Required"
                userEmail.requestFocus()
                //  return
            }
            if (dob.isEmpty()) {
                userDOB.error = "DOB Required"
                userDOB.requestFocus()
                //return
            }
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                userEmail.error = "Valid Email Required"
                userEmail.requestFocus()
                //return@setOnClickListener
            }*/


        }
        else if(next.text=="Submit"){
            second.visibility = View.GONE
            third.visibility = View.VISIBLE
            val email: String = userEmail.text.toString()
            val dob: String = userDOB.text.toString()
            val gender: String = userGender.text.toString()
            if (email.isEmpty()) {

                userEmail.error = "Email Required"
                userEmail.requestFocus()
                //  return
            }
            if (dob.isEmpty()) {
                userDOB.error = "DOB Required"
                userDOB.requestFocus()
                //return
            }
            if (gender.isEmpty()) {
                userGender.error = "Gender Required"
                userGender.requestFocus()
                //return
            }
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                userEmail.error = "Valid Email Required"
                userEmail.requestFocus()
                //return@setOnClickListener
            }
            else{
                next.text="Saved"
            }
        }
    }

    private fun registerUser(phone: String, name: String) {
        //  progressbar.visibility = View.VISIBLE
        mAuth.createUserWithEmailAndPassword(phone, name)
            .addOnCompleteListener(this) { task ->
                //  progressbar.visibility = View.GONE
                if (task.isSuccessful) {
                    addUser(phone, name)
                    //  login()
                } else {
                    task.exception?.message?.let {
                        //  toast(it)
                    }
                }
            }
    }

    private fun addUser(
        phone: String, name: String

    ) {
        ref = FirebaseDatabase.getInstance().reference
        val userId = (ref.push().key).toString()
        val addUser = Data(phone, name)
        ref.child("users").child(phone).setValue(addUser)
        Toast.makeText(this, "Registration Successful", Toast.LENGTH_LONG).show()
    }
}

