package com.clgproject.myapplicationkoitlin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthProvider

class VerifyActivity : AppCompatActivity() {

    private lateinit var editTextOTP: EditText
    private lateinit var buttonVerifyOTP: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_verify)

        editTextOTP = findViewById(R.id.editTextOTP)
        buttonVerifyOTP = findViewById(R.id.buttonVerifyOTP)
        // Get the verification ID from the intent
        var verificationId = intent.getStringExtra("verificationId")

        buttonVerifyOTP.setOnClickListener()
        {
            val otp = editTextOTP.text.toString().trim()
            if (otp.isEmpty()) {
                Toast.makeText(this@VerifyActivity, "Please enter OTP", Toast.LENGTH_SHORT).show()
            } else {
                val credential = PhoneAuthProvider.getCredential(verificationId.toString(), otp)
//                signInWithPhoneAuthCredential(credential)
                if (verificationId != null) {
                    verifyPhoneNumber(verificationId, otp)
                }

            }
        }
    }



    private fun verifyPhoneNumber(verificationId: String, code: String) {
        val credential = PhoneAuthProvider.getCredential(verificationId, code)

        // Sign in the user with the credential
        FirebaseAuth.getInstance().signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // The user is signed in successfully
                    // Move to the next activity or perform any other required action
                    Toast.makeText(this@VerifyActivity, "OTP Verified", Toast.LENGTH_LONG).show()
                    val intent = Intent(this, UploadImage::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    // Sign in failed, display an error message to the user
                    Toast.makeText(this, "Incorrect OTP, Try again.", Toast.LENGTH_LONG).show()
                    finish()
                }
            }
    }
}
