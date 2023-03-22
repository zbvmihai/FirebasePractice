package com.zabava.firebase

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.zabava.firebase.databinding.ActivityAddUserBinding

class AddUserActivity : AppCompatActivity() {

    private lateinit var addUserBinding : ActivityAddUserBinding

    private val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    private val myReference: DatabaseReference = database.reference.child("MyUsers")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addUserBinding = ActivityAddUserBinding.inflate(layoutInflater)
        val view = addUserBinding.root
        setContentView(view)

        supportActionBar?.title = "Add User"

        addUserBinding.buttonAddUser.setOnClickListener {

            addUserToDatabase()

        }
    }

    private fun addUserToDatabase(){

        val name: String = addUserBinding.etName.text.toString()
        val age: Int = addUserBinding.etAge.text.toString().toInt()
        val email: String = addUserBinding.etEmail.text.toString()

        val id: String = myReference.push().key.toString()

        val user = Users(id,name,age,email)

        myReference.child(id).setValue(user).addOnCompleteListener { task ->
            if (task.isSuccessful){
            Toast.makeText(applicationContext,
                "The new user has been added to the database",
                Toast.LENGTH_SHORT).show()
                finish()
            }else{
                Toast.makeText(applicationContext,
                    "Failed to add the new user to the database",
                    Toast.LENGTH_SHORT).show()
                Log.e("Error:",task.exception.toString())
            }
        }

    }
}