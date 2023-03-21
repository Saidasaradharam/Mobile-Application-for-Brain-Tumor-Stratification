package com.clgproject.myapplicationkoitlin

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class HomeActivity : AppCompatActivity() {

    private lateinit var learnButton: Button
    private lateinit var predictButton: Button
    private lateinit var logOutButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        val heading = findViewById<ImageView>(R.id.heading)
        heading.bringToFront()

        learnButton = findViewById(R.id.learn_button)
        predictButton = findViewById(R.id.predict_button)
        logOutButton = findViewById(R.id.logout_button)
        logOutButton.bringToFront()


        learnButton.setOnClickListener {
            val intent = Intent(this, LearnActivity::class.java)
            startActivity(intent)
        }

        predictButton.setOnClickListener {
            val intent = Intent(this, UploadImage::class.java)
            startActivity(intent)
        }

        logOutButton.setOnClickListener {
            FirebaseAuth.getInstance().signOut();
            Toast.makeText(this@HomeActivity, "Logged Out", Toast.LENGTH_LONG).show()
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }


    }
}
