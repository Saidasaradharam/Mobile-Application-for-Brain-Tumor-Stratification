package com.clgproject.myapplicationkoitlin

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity


class LearnActivity : AppCompatActivity() {

    private lateinit var cancerButton: Button
    private lateinit var nonCancerButton: Button
    private lateinit var infoTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_learn)

        infoTextView = findViewById(R.id.tv_info)
        infoTextView.text = "Brain tumors are abnormal growths of cells in the brain. There are many types of brain tumors, and they can be either cancerous (malignant) or non-cancerous (benign). Some common symptoms of brain tumors include headaches, seizures, and changes in vision or hearing. Treatment options for brain tumors include surgery, radiation therapy, and chemotherapy."
        cancerButton = findViewById(R.id.cancer_button)
        nonCancerButton = findViewById(R.id.non_cancer_button)

        cancerButton.setOnClickListener {
            val intent = Intent(this, CancerPage::class.java)
            startActivity(intent)
        }

        nonCancerButton.setOnClickListener {
            val intent = Intent(this, NonCancerPage::class.java)
            startActivity(intent)
        }
    }
}