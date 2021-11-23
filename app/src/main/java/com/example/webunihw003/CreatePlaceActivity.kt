package com.example.webunihw003

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.webunihw003.databinding.ActivityCreatePlaceBinding
import com.example.webunihw003.entity.Place
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.io.ByteArrayOutputStream
import java.net.URLEncoder
import java.util.*

class CreatePlaceActivity : AppCompatActivity() {
    companion object {
        final const val COLLECTION_POSTS = "places"
        final const val PERMISSION_REQUEST_CODE = 1001
        final const val CAMERA_REQUEST_CODE = 1002
    }

    lateinit var binding: ActivityCreatePlaceBinding
    var uploadBitmap: Bitmap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreatePlaceBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.buttonSend.setOnClickListener() {
            if (uploadBitmap == null) {
                uploadPost()
            } else {
                try {
                    uploadPostWithImage()
                } catch (e: java.lang.Exception) {
                    e.printStackTrace()
                }
            }
        }

        binding.buttonAttach.setOnClickListener {
            startActivityForResult(
                Intent(MediaStore.ACTION_IMAGE_CAPTURE),
                CAMERA_REQUEST_CODE
            )
        }

        requestNeededPermission()


    }//ONCREATE

    private fun uploadPost(imageUrl: String = "") {
        val place = Place(
            FirebaseAuth.getInstance().currentUser!!.uid,
            FirebaseAuth.getInstance().currentUser!!.email!!,
            binding.edittextTitle.text.toString(),
            binding.edittextCity.text.toString(),
            binding.edittextStreet.text.toString(),
            binding.edittextInfo.text.toString(),
            imageUrl
        )

        var postsCollection = FirebaseFirestore.getInstance().collection(
            CreatePlaceActivity.COLLECTION_POSTS
        )

        postsCollection.add(place)
            .addOnSuccessListener {
                finish()
            }
            .addOnFailureListener {
                Toast.makeText(
                    this@CreatePlaceActivity,
                    "Error ${it.message}", Toast.LENGTH_LONG
                ).show()
            }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CAMERA_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            uploadBitmap = data!!.extras!!.get("data") as Bitmap
            binding.imgageviewPhoto.setImageBitmap(uploadBitmap)
            binding.imgageviewPhoto.visibility = View.VISIBLE
        }
    }

    @Throws(Exception::class)
    private fun uploadPostWithImage() {

        val baos = ByteArrayOutputStream()
        uploadBitmap?.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val imageInBytes = baos.toByteArray()

        val storageRef = FirebaseStorage.getInstance().getReference()
        val newImage = URLEncoder.encode(UUID.randomUUID().toString(), "UTF-8") + ".jpg"
        val newImagesRef = storageRef.child("images/$newImage")

        newImagesRef.putBytes(imageInBytes)
            .addOnFailureListener { exception ->
                Toast.makeText(this@CreatePlaceActivity, exception.message, Toast.LENGTH_SHORT)
                    .show()
            }.addOnSuccessListener { taskSnapshot ->
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.

                newImagesRef.downloadUrl.addOnCompleteListener(object : OnCompleteListener<Uri> {
                    override fun onComplete(task: Task<Uri>) {
                        uploadPost(task.result.toString())
                    }
                })
            }
    }

    private fun requestNeededPermission() {
        if (ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    android.Manifest.permission.CAMERA
                )
            ) {
                Toast.makeText(
                    this,
                    "I need it for camera", Toast.LENGTH_SHORT
                ).show()
            }

            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.CAMERA),
                PERMISSION_REQUEST_CODE
            )
        } else {
            // we already have permission
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            PERMISSION_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                } else {
                    Toast.makeText(this, "CAMERA perm NOT granted", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }


}