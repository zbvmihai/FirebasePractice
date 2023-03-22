package com.zabava.firebase

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.zabava.firebase.databinding.ActivityUpdateUserBinding

@Suppress("DEPRECATION")
class UpdateUserActivity : AppCompatActivity() {

    private lateinit var updateBinding: ActivityUpdateUserBinding

    private val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    private val myReference: DatabaseReference = database.reference.child("MyUsers")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        updateBinding = ActivityUpdateUserBinding.inflate(layoutInflater)
        val view = updateBinding.root
        setContentView(view)

        supportActionBar?.title = "Update User"

        getAndSetData()

        updateBinding.buttonUpdateUser.setOnClickListener {

            updateData()

        }
    }

    private fun getAndSetData() {

        val usersList: ArrayList<Users>? = intent.getParcelableArrayListExtra("usersList")
        val position = intent.getIntExtra("position", 0)

        val name = usersList!![position].userName
        val age = usersList[position].userAge.toString()
        val email = usersList[position].userEmail

        updateBinding.etUpdateName.setText(name)
        updateBinding.etUpdateAge.setText(age)
        updateBinding.etUpdateEmail.setText(email)

    }

    private fun updateData() {

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

        myReference.child(userId).updateChildren(userMap).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(applicationContext, "The user has been updated", Toast.LENGTH_SHORT)
                    .show()
                finish()
            } else {
                Log.e("Error: ", task.exception.toString())
            }
        }
    }
}