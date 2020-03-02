package com.tragicbytes.midi

import android.graphics.Color
import android.os.Bundle
import android.text.TextUtils
import android.util.Patterns
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import com.chaos.view.PinView
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_mobile_number.*
import java.util.concurrent.TimeUnit


class MobileNumber : FirebaseConfig(), View.OnClickListener {
    private val TAG = "PhoneAuthActivity"
    private lateinit var mAuth: FirebaseAuth
    lateinit var ref: DatabaseReference
    private var storedVerificationId: String? = null
    private lateinit var resendToken: PhoneAuthProvider.ForceResendingToken
    private lateinit var callbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks


    private lateinit var pinView: PinView
    private lateinit var next: Button
    private lateinit var topText: TextView
    private lateinit var textU: TextView
    private lateinit var userName: EditText
    private lateinit var userPhone: EditText
    private lateinit var first: ConstraintLayout
    private lateinit var second: ConstraintLayout
    private lateinit var third: ConstraintLayout
    private var verificationInProgress = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mobile_number)
        mAuth = FirebaseAuth.getInstance()
        ref = FirebaseDatabase.getInstance().getReference("users")

        //region Elements Typecasting
        topText = findViewById(R.id.topText)
        pinView = findViewById(R.id.pinView)
        next = findViewById(R.id.button)
        userName = findViewById(R.id.username)
        userPhone = findViewById(R.id.userPhone)
        first = findViewById(R.id.first_step)
        second = findViewById(R.id.secondStep)
        third = findViewById(R.id.thirdStep)
        textU = findViewById(R.id.textView_noti)
        first.visibility = View.VISIBLE
        next.setOnClickListener(this)
        third.setOnClickListener(this)
        third.visibility = View.GONE


        // Initialize phone auth callbacks
        // [START phone_auth_callbacks]
        callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                // This callback will be invoked in two situations:
                // 1 - Instant verification. In some cases the phone number can be instantly
                //     verified without needing to send or enter a verification code.
                // 2 - Auto-retrieval. On some devices Google Play services can automatically
                //     detect the incoming verification SMS and perform verification without
                //     user action.
                Log.d(TAG, "onVerificationCompleted:$credential")
                // [START_EXCLUDE silent]
                verificationInProgress = false
                // [END_EXCLUDE]

                // [START_EXCLUDE silent]
                // Update the UI and attempt sign in with the phone credential
                //updateUI(STATE_VERIFY_SUCCESS, credential)
                Toast.makeText(this@MobileNumber,"phone number verified! Trying to sign you in..",Toast.LENGTH_LONG).show()
                // [END_EXCLUDE]
                signInWithPhoneAuthCredential(credential)
            }

            override fun onVerificationFailed(e: FirebaseException) {
                // This callback is invoked in an invalid request for verification is made,
                // for instance if the the phone number format is not valid.
                Log.w(TAG, "onVerificationFailed", e)
                // [START_EXCLUDE silent]
                verificationInProgress = false
                // [END_EXCLUDE]

                if (e is FirebaseAuthInvalidCredentialsException) {
                    // Invalid request
                    // [START_EXCLUDE]
                    userPhone.error = "Invalid phone number."
                    // [END_EXCLUDE]
                } else if (e is FirebaseTooManyRequestsException) {
                    // The SMS quota for the project has been exceeded
                    // [START_EXCLUDE]
                    Toast.makeText(this@MobileNumber, "Quota exceeded.",Toast.LENGTH_SHORT).show()
                    // [END_EXCLUDE]
                }

                // Show a message and update the UI
                // [START_EXCLUDE]
                //updateUI(STATE_VERIFY_FAILED)
                Toast.makeText(this@MobileNumber,"Verification failed!",Toast.LENGTH_LONG).show()
                // [END_EXCLUDE]
            }

            override fun onCodeSent(
                verificationId: String,
                token: PhoneAuthProvider.ForceResendingToken
            ) {
                // The SMS verification code has been sent to the provided phone number, we
                // now need to ask the user to enter the code and then construct a credential
                // by combining the code with a verification ID.
                Log.d(TAG, "onCodeSent:$verificationId")

                // Save verification ID and resending token so we can use them later
                storedVerificationId = verificationId
                resendToken = token

                // [START_EXCLUDE]
                // Update UI
                //updateUI(STATE_CODE_SENT)
                Toast.makeText(this@MobileNumber,"CODE SENT",Toast.LENGTH_LONG).show()
                // [END_EXCLUDE]
            }
        }
        // [END phone_auth_callbacks]

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
               /* registerUser(
                    phone, name
                )*/
                PhoneAuthProvider.getInstance().verifyPhoneNumber(
                    "+91"+phone, // Phone number to verify
                    60, // Timeout duration
                    TimeUnit.SECONDS, // Unit of timeout
                    this, // Activity (for callback binding)
                    callbacks) // OnVerificationStateChangedCallbacks
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

    // [START sign_in_with_phone]
    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        mAuth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithCredential:success")

                    val user = task.result?.user
                    // [START_EXCLUDE]
                    //updateUI(STATE_SIGNIN_SUCCESS, user.phoneNumber)
                    Toast.makeText(this,user?.phoneNumber,Toast.LENGTH_LONG).show()
                    // [END_EXCLUDE]
                } else {
                    // Sign in failed, display a message and update the UI
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
                        // The verification code entered was invalid
                        // [START_EXCLUDE silent]
                        textU.error = "Invalid code."
                        // [END_EXCLUDE]
                    }
                    // [START_EXCLUDE silent]
                    // Update UI
                    //updateUI(STATE_SIGNIN_FAILED)
                    // [END_EXCLUDE]
                }
            }
    }
    // [END sign_in_with_phone]
}

