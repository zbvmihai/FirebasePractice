package com.zabava.firebase

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.zabava.firebase.databinding.ActivitySignUpBinding

class SignUpActivity : AppCompatActivity() {

    private lateinit var signupBinding: ActivitySignUpBinding

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        signupBinding = ActivitySignUpBinding.inflate(layoutInflater)
        val view = signupBinding.root
        setContentView(view)

        signupBinding.btnSignUpUser.setOnClickListener {

            val userEmail = signupBinding.etEmailSignUp.text.toString()
            val userPassword = signupBinding.etPasswordSignUp.text.toString()

            signupWithFirebase(userEmail,userPassword)

        }
    }

    private fun signupWithFirebase(userEmail: String, userPassword: String){

        auth.createUserWithEmailAndPassword(userEmail,userPassword).addOnCompleteListener { task ->

            if (task.isSuccessful){
                Toast.makeText(applicationContext,"Your account has been created!",Toast.LENGTH_SHORT).show()
                finish()
            }else{
                Toast.makeText(applicationContext,"Error while creating the account",Toast.LENGTH_SHORT).show()
                Log.e("Error: ",task.exception.toString())
            }
        }

    }
}