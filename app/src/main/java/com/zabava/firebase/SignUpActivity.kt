package com.zabava.firebase

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.zabava.firebase.databinding.ActivitySignUpBinding

class SignUpActivity : AppCompatActivity() {

    lateinit var signupBinding: ActivitySignUpBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        signupBinding = ActivitySignUpBinding.inflate(layoutInflater)
        val view = signupBinding.root
        super.onCreate(savedInstanceState)
        setContentView(view)

        signupBinding.btnSignUpUser.setOnClickListener {



        }
    }
}