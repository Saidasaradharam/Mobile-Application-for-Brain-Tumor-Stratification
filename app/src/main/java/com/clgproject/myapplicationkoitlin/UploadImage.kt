package com.clgproject.cerebroscan

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.MediaStore
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.ImageView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import android.Manifest
import android.graphics.Bitmap
import android.widget.TextView
import com.clgproject.cerebroscan.ml.BrainTumor10Epochs
import org.tensorflow.lite.DataType
import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import java.io.File
import java.io.FileInputStream
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel


class UploadImage : AppCompatActivity() {

    private lateinit var btnUpload: Button
    private lateinit var imgPreview: ImageView
    private lateinit var outputText: TextView
    private lateinit var learnMoreText: TextView
    private lateinit var heading: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_upload_image)

        btnUpload = findViewById(R.id.btn_upload)
        imgPreview = findViewById(R.id.img_preview)

        btnUpload.setOnClickListener {
            // Request permission to access device storage
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), REQUEST_CODE_PERMISSION)
            } else {
                // Launch image picker intent
                val intent = Intent(Intent.ACTION_GET_CONTENT)
                intent.type = "image/*"
                val chooseIntent = Intent.createChooser(intent, "Choose an image")
                val googleDriveIntent = Intent(Intent.ACTION_OPEN_DOCUMENT_TREE)
                val chooserIntent = Intent.createChooser(googleDriveIntent, "Select from")
                chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, arrayOf(chooseIntent))
                startActivityForResult(chooserIntent, MainActivity.REQUEST_CODE_PICK_IMAGE)
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == MainActivity.REQUEST_CODE_PERMISSION && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            // Permission granted, launch image picker intent
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "image/*"
            val chooseIntent = Intent.createChooser(intent, "Choose an image")
            val googleDriveIntent = Intent(Intent.ACTION_OPEN_DOCUMENT_TREE)
            val chooserIntent = Intent.createChooser(googleDriveIntent, "Select from")
            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, arrayOf(chooseIntent))
            startActivityForResult(chooserIntent, MainActivity.REQUEST_CODE_PICK_IMAGE)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_PICK_IMAGE && resultCode == RESULT_OK) {
            // Retrieve the image data
            val uri = data?.data
            val bitmap = uri?.let {
                MediaStore.Images.Media.getBitmap(this.contentResolver, it)
            }
            // Generate text output based on the image
            val textOutput = generateTextFromImage(bitmap)

            // Display the text output
            if (textOutput.isNotEmpty()) {
                heading=findViewById(R.id.upload_head)
                heading.text="Scan Successfully Uploaded!"
                outputText = findViewById(R.id.output_text)
                outputText.text = textOutput
                learnMoreText = findViewById(R.id.learn_more_text)
                if(textOutput!="No Tumor"){
                    learnMoreText.setTextColor(ContextCompat.getColor(this, R.color.red))
                    if(textOutput=="Glioma Tumor")
                        learnMoreText.text = "Glioma is a type of brain tumor that can be cancerous or non-cancerous. Cancerous gliomas are referred to as malignant gliomas, and non-cancerous ones are called benign gliomas. Treatment for gliomas can involve surgery, radiation therapy, and/or chemotherapy, depending on the type, size, and location of the tumor, as well as the individual's overall health. However, the specific treatment plan can vary from person to person and should be determined in consultation with a medical professional.\n \n"
                    else if(textOutput=="Meningioma Tumor")
                        learnMoreText.text = "Meningioma is a type of brain tumor that arises from the meninges, which are the protective membranes surrounding the brain and spinal cord. Most meningiomas are non-cancerous (benign), but a small percentage can be cancerous (malignant). Treatment for meningioma depends on several factors, including the size and location of the tumor, as well as the individual's age and overall health. Treatment options may include observation (in cases where the tumor is small and not causing symptoms), surgery to remove the tumor, radiation therapy, and chemotherapy. The prognosis for meningioma is generally good, with a high rate of successful treatment and long-term survival for most patients.\n \n"
                    else
                        learnMoreText.text = "A pituitary tumor is a growth in the pituitary gland, which is a small gland located at the base of the brain. Pituitary tumors can be either cancerous (malignant) or non-cancerous (benign). Most pituitary tumors are non-cancerous and do not spread to other parts of the body.\n" +
                                "\n" +
                                "The treatment for a pituitary tumor depends on the type of tumor, its size, and whether it is causing symptoms. Treatment options include surgery, radiation therapy, medication, or a combination of these approaches. The goal of treatment is to shrink or remove the tumor and relieve any symptoms it may be causing. The treatment plan will be tailored to each individual's specific needs and circumstances, and will be determined by a team of healthcare professionals.\n \n"
                }
                else{
                    learnMoreText.setTextColor(ContextCompat.getColor(this, R.color.green))
                    learnMoreText.text="You are safe, To be sure consult a Doctor.\n \n"
                }
//                val snackbar = Snackbar.make(imgPreview, textOutput, Snackbar.LENGTH_LONG)
//                snackbar.show()
            }
            // Display the image in the preview
            bitmap?.let {
                imgPreview.setImageBitmap(it)
            }
        }
    }

    private fun generateTextFromImage(bitmap: Bitmap?): String {
        // Load the model from the file
        val model = Interpreter(loadModelFile("BrainTumorModelSaved.tflite"))

        // Preprocess the input image
        val inputWidth = 128
        val inputHeight = 128
        val resizedBitmap = Bitmap.createScaledBitmap(bitmap!!, inputWidth, inputHeight, true)
        val inputTensor = TensorImage.fromBitmap(resizedBitmap)

        // Run inference on the input image
        val outputTensor = TensorBuffer.createFixedSize(intArrayOf(1, 4), DataType.FLOAT32)
        model.run(inputTensor.buffer, outputTensor.buffer)

        // Get the predicted label from the output tensor
        val scores = outputTensor.floatArray
        val labelDict = arrayOf("Glioma Tumor", "No Tumor", "Meningioma Tumor", "Pituitary Tumor")
        val predictedClassIndex = scores.indices.maxByOrNull { scores[it] } ?: -1
        val predictedLabel = if (predictedClassIndex >= 0) labelDict[predictedClassIndex] else "Unknown"

        // Return the predicted label as text output
        return predictedLabel

    }

    private fun loadModelFile(modelPath: String): MappedByteBuffer {
        val assetFileDescriptor = assets.openFd(modelPath)
        val inputStream = FileInputStream(assetFileDescriptor.fileDescriptor)
        val fileChannel = inputStream.channel
        val startOffset = assetFileDescriptor.startOffset
        val declaredLength = assetFileDescriptor.declaredLength
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
    }



    companion object {
        private const val REQUEST_CODE_PERMISSION = 1
        private const val REQUEST_CODE_PICK_IMAGE = 2
    }
}