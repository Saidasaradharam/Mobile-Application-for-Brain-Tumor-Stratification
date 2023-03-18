package com.clgproject.cerebroscan

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import org.w3c.dom.Text
import java.util.concurrent.TimeUnit


class MainActivity : AppCompatActivity() {

    private lateinit var editTextPhoneNumber: EditText
    private lateinit var buttonGenerateOTP: Button

    private lateinit var verificationId: String
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        FirebaseApp.initializeApp(this)
        auth = FirebaseAuth.getInstance()


        editTextPhoneNumber = findViewById(R.id.editTextPhoneNumber)
        buttonGenerateOTP = findViewById(R.id.buttonGenerateOTP)


        buttonGenerateOTP.setOnClickListener {
            val phoneNumber = "+91${editTextPhoneNumber.text}" // Replace with your own country code and phone number field
            val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                    // This callback is called when the verification is done without user interaction.
                    signInWithPhoneAuthCredential(credential)
                }

                override fun onVerificationFailed(e: FirebaseException) {
                    // This callback is invoked in an invalid request for verification is made,
                    // for instance if the the phone number format is not valid.
                    Toast.makeText(this@MainActivity, "Verification failed: ${e.message}", Toast.LENGTH_SHORT).show()
                }

                override fun onCodeSent(
                    verificationId: String,
                    token: PhoneAuthProvider.ForceResendingToken
                ) {
                    // The SMS verification code has been sent to the provided phone number, we
                    // now need to ask the user to enter the code and then construct a credential
                    // by combining the code with a verification ID.
                    this@MainActivity.verificationId = verificationId // Save verificationId as a member variable
                    val intent = Intent(this@MainActivity, VerifyActivity::class.java)
                    intent.putExtra("verificationId", verificationId)
                    startActivity(intent)
                }
            }

            PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,
                60, // Timeout duration
                TimeUnit.SECONDS,
                this,
                callbacks
            )
        }



    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this, OnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    val user = task.result?.user
                    if (user != null) {
                        Toast.makeText(this@MainActivity, "Authentication successful", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    // Sign in failed, display a message and update the UI
                    Toast.makeText(this@MainActivity, "Authentication failed", Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun startPhoneNumberVerification(phoneNumber: String, callbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks) {
        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phoneNumber)       // Phone number to verify
            .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
            .setActivity(this)                 // Activity (for callback binding)
            .setCallbacks(callbacks)           // OnVerificationStateChangedCallbacks
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    companion object {
        const val REQUEST_CODE_PERMISSION = 1
        const val REQUEST_CODE_PICK_IMAGE = 2
    }
}
