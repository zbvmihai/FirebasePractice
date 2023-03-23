package com.zabava.firebase

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.zabava.firebase.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {

    private lateinit var loginBinding: ActivityLoginBinding

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loginBinding = ActivityLoginBinding.inflate(layoutInflater)
        val view = loginBinding.root
        setContentView(view)

        loginBinding.btnLogin.setOnClickListener {

            val userEmail = loginBinding.etEmailLogin.text.toString()
            val userPassword = loginBinding.etPasswordLogin.text.toString()

            loginWithFirebase(userEmail,userPassword)

        }

        loginBinding.btnSignUp.setOnClickListener {
            val intent = Intent(this@LoginActivity, SignUpActivity::class.java)
            startActivity(intent)
        }

        loginBinding.btnForgotPassword.setOnClickListener {

            val intent = Intent(this, ForgetActivity::class.java)
            startActivity(intent)

        }

        loginBinding.BtnLoginWithPhone.setOnClickListener {
            val intent = Intent(this, PhoneActivity::class.java)
            startActivity(intent)
        }
    }



    private fun loginWithFirebase(userEmail: String, userPassword: String){

        auth.signInWithEmailAndPassword(userEmail, userPassword)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Toast.makeText(applicationContext,"Login is successful",Toast.LENGTH_SHORT).show()
                    val intent = Intent(this@LoginActivity,MainActivity::class.java)
                    startActivity(intent)
                    finish()

                } else {
                    Toast.makeText(applicationContext,"Failed to Login!",Toast.LENGTH_SHORT).show()
                    Log.e("Error: ", task.exception.toString())
                }
            }
    }

    override fun onStart() {
        super.onStart()

        val user = auth.currentUser

        if (user != null) {

            val intent = Intent(this@LoginActivity,MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}