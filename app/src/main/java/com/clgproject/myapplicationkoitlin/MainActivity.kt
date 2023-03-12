package com.clgproject.myapplicationkoitlin

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
import com.clgproject.myapplicationkoitlin.ml.BrainTumor10Epochs
import org.tensorflow.lite.DataType
import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import java.io.File
import java.io.FileInputStream
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel


class MainActivity : AppCompatActivity() {

    private lateinit var btnUpload: Button
    private lateinit var imgPreview: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

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
                val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                startActivityForResult(intent, REQUEST_CODE_PICK_IMAGE)
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSION && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            // Permission granted, launch image picker intent
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent, REQUEST_CODE_PICK_IMAGE)
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
                val snackbar = Snackbar.make(imgPreview, textOutput, Snackbar.LENGTH_LONG)
                snackbar.show()
            }
            // Display the image in the preview
            bitmap?.let {
                imgPreview.setImageBitmap(it)
            }
        }
    }

    private fun generateTextFromImage(bitmap: Bitmap?): String {
        // Load the model from the file
        val model = Interpreter(loadModelFile("BrainTumor10Epochs.tflite"))

        // Preprocess the input image
        val inputWidth = 224
        val inputHeight = 224
        val resizedBitmap = Bitmap.createScaledBitmap(bitmap!!, inputWidth, inputHeight, true)
        val inputTensor = TensorImage.fromBitmap(resizedBitmap)
        inputTensor.buffer.rewind()

        // Run inference on the input image
        val outputTensor = TensorBuffer.createFixedSize(intArrayOf(1, 2), DataType.FLOAT32)
        model.run(inputTensor.buffer, outputTensor.buffer)

        // Get the predicted label from the output tensor
        val scores = outputTensor.floatArray
        val predictedLabel = if (scores[0] > scores[1]) "No" else "Yes"

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
