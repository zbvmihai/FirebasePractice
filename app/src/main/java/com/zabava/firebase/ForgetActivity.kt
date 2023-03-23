package com.zabava.firebase

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.zabava.firebase.databinding.ActivityForgetBinding

class ForgetActivity : AppCompatActivity() {

    private lateinit var forgetBinding: ActivityForgetBinding

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        forgetBinding = ActivityForgetBinding.inflate(layoutInflater)
        val view = forgetBinding.root
        setContentView(view)

        forgetBinding.btnResetPassword.setOnClickListener {

        val email = forgetBinding.etForgotEmail.text.toString()

            auth.sendPasswordResetEmail(email).addOnCompleteListener { task ->

                if (task.isSuccessful){
                    Toast.makeText(this,"Reset email sent to $email",Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
        }
    }
}