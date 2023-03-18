package com.clgproject.cerebroscan

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
        infoTextView.text = "A cancerous brain tumor, also known as a malignant brain tumor, is a fast-growing and aggressive abnormal growth of cells in the brain. Cancerous brain tumors can spread to other parts of the brain and the body, making them much more dangerous than non-cancerous tumors. Symptoms of cancerous brain tumors can include headaches, seizures, personality changes, weakness, and vision or hearing problems. Treatment options for cancerous brain tumors include surgery, radiation therapy, chemotherapy, targeted therapy, and immunotherapy, depending on the tumor's location, size, and type. The prognosis for cancerous brain tumors can vary depending on many factors, including the type and stage of the tumor, the person's age and overall health, and the treatment received. Gliomas are tumors that arise from the glial cells of the brain and can be highly malignant and aggressive. Meningiomas, on the other hand, are tumors that arise from the meninges, the layers of tissue that cover the brain and spinal cord. "
        heading = findViewById(R.id.cancer_head)
        heading.text = "Malignant Tumor"
    }
}