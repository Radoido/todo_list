package com.main.todo_list


import android.app.Activity

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.main.todo_list.databinding.ActivityCreateLivroBinding
import com.main.todo_list.databinding.ActivityMainBinding
import java.io.File
import java.io.FileOutputStream

class CreateLivro : AppCompatActivity() {

    private var imgUri: Uri? = null
    private var titulo: String? = null
    private var author: String? = null

    private val PICK_IMAGE_REQUEST = 1
    private val REQUEST_MEDIA_PERMISSION = 1001

    private lateinit var binding: ActivityCreateLivroBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateLivroBinding.inflate(layoutInflater)
        setContentView(binding.createView)


        binding.btnImg.setOnClickListener {
            checkMediaPermission()
        }

    }

    private fun checkMediaPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) { // Android 13+ (API 33)
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES)
                != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.READ_MEDIA_IMAGES),
                    REQUEST_MEDIA_PERMISSION
                )
            } else {
                openImagePicker()
            }
        } else { // For Android versions below Android 13
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    REQUEST_MEDIA_PERMISSION
                )
            } else {
                openImagePicker()
            }
        }
    }
    private fun openImagePicker() {
        val pickImageIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(pickImageIntent, PICK_IMAGE_REQUEST)
    }

    // Handle the result of image selection
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK && requestCode == PICK_IMAGE_REQUEST) {
            // Get the image URI and display it
            val imageUri: Uri? = data?.data
            imageUri?.let { uri ->handleImageUri(uri)

            }
        }
    }
    private fun handleImageUri(uri: Uri) {
        // Get the app's internal storage directory (private folder)
        val appContext = applicationContext
        val folder = File(appContext.filesDir, "images")  // Create a folder called "images" inside internal storage

        if (!folder.exists()) {
            folder.mkdirs()  // Make sure the folder exists
        }

        // Generate a new file in the internal storage
        val fileName = "uploadedimage${System.currentTimeMillis()}.jpg"
        val newFile = File(folder, fileName)

        try {
            // Open input stream to read the image
            val inputStream = contentResolver.openInputStream(uri)
            val outputStream = FileOutputStream(newFile)

            inputStream?.use { input ->
                outputStream.use { output ->
                    // Copy the content of the image to the new file
                    input.copyTo(output)
                }
            }

            // Successfully saved the image to internal storage
            Toast.makeText(this, "Image saved to app's internal storage", Toast.LENGTH_SHORT).show()

            val savedImageUri = Uri.fromFile(newFile)

            imgUri = savedImageUri

        } catch (e: Exception) {
            // Handle the exception if something goes wrong
            Toast.makeText(this, "Failed to save image: ${e.message}", Toast.LENGTH_LONG).show()
            e.printStackTrace()
        }
    }


    // Handle the result of the media permission request
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == REQUEST_MEDIA_PERMISSION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, open the image picker
                openImagePicker()
            } else {
                // Permission denied, show a message
                Toast.makeText(this, "Permission denied to read your images", Toast.LENGTH_SHORT).show()
            }
        }
    }
}