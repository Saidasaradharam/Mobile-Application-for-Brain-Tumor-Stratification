package com.clgproject.myapplicationkoitlin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthProvider

class VerifyActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_verify)

        // Get the verification ID from the intent
        val verificationId = intent.getStringExtra("verificationId")

        // Verify the user's phone number with the verification code
        if (verificationId != null) {
            verifyPhoneNumber(verificationId, "123456")
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
                    val intent = Intent(this, UploadImage::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    // Sign in failed, display an error message to the user
                    Toast.makeText(this, "Authentication failed", Toast.LENGTH_SHORT).show()
                }
            }
    }
}
