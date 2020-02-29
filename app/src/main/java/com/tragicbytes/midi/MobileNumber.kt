package com.tragicbytes.midi

import android.graphics.Color
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import com.chaos.view.PinView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthProvider


@Suppress("UNREACHABLE_CODE")
class MobileNumber : FirebaseConfig() , View.OnClickListener{
    var auth = FirebaseAuth.getInstance()
    //  auth.setLanguageCode(Locale.getDefault().language)
    private var storedVerificationId: String? = null
    private lateinit var resendToken: PhoneAuthProvider.ForceResendingToken

    private lateinit var pinView: PinView
    private lateinit var next: Button
    private lateinit var topText:TextView
    private lateinit var textU: TextView
    private lateinit var userName:EditText
    private lateinit var userPhone:EditText
    private lateinit var  first:ConstraintLayout
    private lateinit var second:ConstraintLayout
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mobile_number)


        topText = findViewById(R.id.topText);
        pinView = findViewById(R.id.pinView);
        next = findViewById(R.id.button);
        userName = findViewById(R.id.username);
        userPhone = findViewById(R.id.password);
        first = findViewById(R.id.first_step);
        second = findViewById(R.id.secondStep);
        textU = findViewById(R.id.textView_noti);
        first.setVisibility(View.VISIBLE);
        next.setOnClickListener(this);
    }

    override fun onClick(v: View?) {
//        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        if (next.text == "Let's go!") {
            val name: String = userName.text.toString()
            val phone: String = userPhone.text.toString()
            if (!TextUtils.isEmpty(name) && !TextUtils.isEmpty(phone)) {
                next.text = "Verify"
                first.visibility = View.GONE
                second.visibility = View.VISIBLE
                topText.text = "I Still don't trust you.\nTell me something that only two of us know."
            } else {
                Toast.makeText(this, "Please enter the details", Toast.LENGTH_SHORT)
                    .show()
            }
        } else if (next.getText().equals("Verify")) {
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
        }
    }
}
