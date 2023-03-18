package com.clgproject.cerebroscan

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView

class NonCancerPage : AppCompatActivity() {

    private lateinit var infoTextView: TextView
    private lateinit var heading: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_non_cancer_page)

        infoTextView = findViewById(R.id.non_cancer_info)
        infoTextView.text = "A non-cancerous brain tumor, also known as a benign brain tumor, is an abnormal growth of cells in the brain that does not spread to other parts of the body. Although non-cancerous brain tumors are not as aggressive as cancerous tumors, they can still cause significant health problems depending on their size and location in the brain. Some common symptoms of non-cancerous brain tumors include headaches, seizures, nausea, and changes in vision or hearing. Treatment options for non-cancerous brain tumors include surgical removal, radiation therapy, and chemotherapy, depending on the tumor's size, location, and other factors. Prognosis and outcome for non-cancerous brain tumors depend on the type of tumor, its location, and the treatment received. Pituitary tumors are non-cancerous (benign) tumors, which means that they do not spread to other parts of the body. However, pituitary tumors can still cause significant health problems due to their location in the brain and their effect on the body's hormone balance."
        heading = findViewById(R.id.non_cancer_head)
        heading.text = "Benign Tumor"

    }
}