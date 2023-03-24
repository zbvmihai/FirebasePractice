package com.zabava.firebase

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.squareup.picasso.Picasso
import com.zabava.firebase.databinding.ActivityUpdateUserBinding
import java.util.*

@Suppress("DEPRECATION")
class UpdateUserActivity : AppCompatActivity() {

    private lateinit var updateBinding: ActivityUpdateUserBinding

    private var usersList: ArrayList<Users> = arrayListOf()
    private var position : Int = 0

    private val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    private val myReference: DatabaseReference = database.reference.child("MyUsers")

    private lateinit var activityResultLauncher: ActivityResultLauncher<Intent>

    private var imageUri: Uri? = null

    private val firebaseStorage: FirebaseStorage = FirebaseStorage.getInstance()
    private val storageReference: StorageReference = firebaseStorage.reference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        updateBinding = ActivityUpdateUserBinding.inflate(layoutInflater)
        val view = updateBinding.root
        setContentView(view)

        supportActionBar?.title = "Update User"

        registerActivityForResult()

        getAndSetData()

        updateBinding.buttonUpdateUser.setOnClickListener {

            uploadPhoto()

        }

        updateBinding.ivUpdateUserImage.setOnClickListener {

            chooseImage()

        }
    }

    private fun getAndSetData() {

        usersList = intent.getParcelableArrayListExtra("usersList")!!
        position = intent.getIntExtra("position", 0)

        val name = usersList[position].userName
        val age = usersList[position].userAge.toString()
        val email = usersList[position].userEmail
        val imageUrl = usersList[position].url

        updateBinding.etUpdateName.setText(name)
        updateBinding.etUpdateAge.setText(age)
        updateBinding.etUpdateEmail.setText(email)
        Picasso.get().load(imageUrl).into(updateBinding.ivUpdateUserImage)

    }

    private fun updateData(imageUrl: String, imageName: String) {

        val usersList: ArrayList<Users>? = intent.getParcelableArrayListExtra("usersList")
        val position = intent.getIntExtra("position", 0)

        val updatedName = updateBinding.etUpdateName.text.toString()
        val updatedAge = updateBinding.etUpdateAge.text.toString().toInt()
        val updatedEmail = updateBinding.etUpdateEmail.text.toString()
        val userId = usersList!![position].userId

        val userMap = mutableMapOf<String, Any>()
        userMap["userId"] = userId
        userMap["userName"] = updatedName
        userMap["userAge"] = updatedAge
        userMap["userEmail"] = updatedEmail
        userMap["url"] = imageUrl
        userMap["imageName"] = imageName

        myReference.child(userId).updateChildren(userMap).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(applicationContext, "The user has been updated", Toast.LENGTH_SHORT)
                    .show()

                updateBinding.buttonUpdateUser.isClickable = true
                updateBinding.pbUpdateUser.visibility = View.INVISIBLE

                finish()
            } else {
                Log.e("Error: ", task.exception.toString())
            }
        }
    }

    private fun chooseImage() {

            val intent = Intent()
            intent.type = "image/*"
            intent.action = Intent.ACTION_GET_CONTENT
            activityResultLauncher.launch(intent)
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

                    Picasso.get().load(it).into(updateBinding.ivUpdateUserImage)

                }
            }
        }
    }

    private fun uploadPhoto() {

        updateBinding.buttonUpdateUser.isClickable = false
        updateBinding.pbUpdateUser.visibility = View.VISIBLE

        val imageName = usersList[position].imageName

        val imageReference = storageReference.child("UsersImages").child(imageName)

        imageUri?.let { uri ->

            imageReference.putFile(uri).addOnSuccessListener {
                Toast.makeText(applicationContext, "Image Updated", Toast.LENGTH_SHORT).show()

                val myUploadImageReference = storageReference.child("UsersImages").child(imageName)

                myUploadImageReference.downloadUrl.addOnSuccessListener { url ->

                    val imageURL = url.toString()

                    updateData(imageURL,imageName)
                }

            }.addOnFailureListener {
                Toast.makeText(applicationContext, "Upload failed", Toast.LENGTH_SHORT).show()
                it.localizedMessage?.let { it1 -> Log.e("Error: ", it1) }
            }
        }
    }
}