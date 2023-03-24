package com.zabava.firebase

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.squareup.picasso.Picasso
import com.zabava.firebase.databinding.ActivityAddUserBinding
import java.util.*

class AddUserActivity : AppCompatActivity() {

    private lateinit var addUserBinding: ActivityAddUserBinding

    private lateinit var activityResultLauncher: ActivityResultLauncher<Intent>

    private var imageUri: Uri? = null

    private val firebaseStorage: FirebaseStorage = FirebaseStorage.getInstance()
    private val storageReference: StorageReference = firebaseStorage.reference

    private val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    private val myReference: DatabaseReference = database.reference.child("MyUsers")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addUserBinding = ActivityAddUserBinding.inflate(layoutInflater)
        val view = addUserBinding.root
        setContentView(view)

        supportActionBar?.title = "Add User"

        registerActivityForResult()

        addUserBinding.buttonAddUser.setOnClickListener {
            uploadPhoto()
        }

        addUserBinding.ivUserImage.setOnClickListener {
            chooseImage()
        }
    }

    private fun chooseImage() {

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED
        ) {

            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                1
            )

        } else {

            val intent = Intent()
            intent.type = "image/*"
            intent.action = Intent.ACTION_GET_CONTENT
            activityResultLauncher.launch(intent)

        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == 1 && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            val intent = Intent()
            intent.type = "image/*"
            intent.action = Intent.ACTION_GET_CONTENT
            activityResultLauncher.launch(intent)
        } else {
            Toast.makeText(applicationContext,"Permissions Denied. Go to application settings to enable it.",Toast.LENGTH_SHORT).show()
        }
    }

    private fun registerActivityForResult() {

        activityResultLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->

            val resultCode = result.resultCode
            val imageData = result.data

            if (resultCode == RESULT_OK && imageData != null) {

                imageUri = imageData.data
                imageUri?.let {

                    Picasso.get().load(it).into(addUserBinding.ivUserImage)

                }
            }
        }
    }

    private fun addUserToDatabase(url: String) {

        val name: String = addUserBinding.etName.text.toString()
        val age: Int = addUserBinding.etAge.text.toString().toInt()
        val email: String = addUserBinding.etEmail.text.toString()

        val id: String = myReference.push().key.toString()

        val user = Users(id, name, age, email,url)

        myReference.child(id).setValue(user).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(
                    applicationContext,
                    "The new user has been added to the database",
                    Toast.LENGTH_SHORT
                ).show()
                finish()

                addUserBinding.buttonAddUser.isClickable = true
                addUserBinding.pbAddUser.visibility = View.INVISIBLE
            } else {
                Toast.makeText(
                    applicationContext,
                    "Failed to add the new user to the database",
                    Toast.LENGTH_SHORT
                ).show()
                Log.e("Error:", task.exception.toString())
            }
        }
    }

    private fun uploadPhoto() {

        addUserBinding.buttonAddUser.isClickable = false
        addUserBinding.pbAddUser.visibility = View.VISIBLE

        val imageName = UUID.randomUUID().toString()

        val imageReference = storageReference.child("UsersImages").child(imageName)

        imageUri?.let { uri ->

            imageReference.putFile(uri).addOnSuccessListener {
                Toast.makeText(applicationContext,"Image uploaded",Toast.LENGTH_SHORT).show()

                val myUploadImageReference = storageReference.child("UsersImages").child(imageName)

                myUploadImageReference.downloadUrl.addOnSuccessListener { url ->

                    val imageURL = url.toString()

                    addUserToDatabase(imageURL)
                }

            }.addOnFailureListener{
                Toast.makeText(applicationContext,"Upload failed",Toast.LENGTH_SHORT).show()
                it.localizedMessage?.let { it1 -> Log.e("Error: ", it1) }
            }
        }
    }
}