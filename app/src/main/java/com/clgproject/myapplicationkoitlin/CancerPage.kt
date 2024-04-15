package com.clgproject.myapplicationkoitlin

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView

class CancerPage : AppCompatActivity() {

    private lateinit var infoTextView: TextView
    private lateinit var heading: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cancer_page)

        infoTextView = findViewById(R.id.cancer_info)
        infoTextView.text = getString(R.string.cancer_info) // Use the string resource for cancer info
        heading = findViewById(R.id.cancer_head)
        heading.text = getString(R.string.malignant_tumor) // Use the string resource for the heading
    }
}