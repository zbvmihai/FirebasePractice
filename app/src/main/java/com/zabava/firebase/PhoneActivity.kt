package com.zabava.firebase

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.zabava.firebase.databinding.ActivityPhoneBinding
import java.util.concurrent.TimeUnit

class PhoneActivity : AppCompatActivity() {

    private lateinit var phoneBinding: ActivityPhoneBinding

    private val auth : FirebaseAuth = FirebaseAuth.getInstance()

    private lateinit var mCallbacks : PhoneAuthProvider.OnVerificationStateChangedCallbacks

    var verificationCode = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        phoneBinding = ActivityPhoneBinding.inflate(layoutInflater)
        val view = phoneBinding.root
        super.onCreate(savedInstanceState)
        setContentView(view)

        phoneBinding.btnSendSms.setOnClickListener {

            val userPhoneNumber = phoneBinding.etPhoneNumber.text.toString()

            val options = PhoneAuthOptions.newBuilder(auth)
                .setPhoneNumber(userPhoneNumber)
                .setTimeout(60L, TimeUnit.SECONDS)
                .setActivity(this@PhoneActivity)
                .setCallbacks(mCallbacks)
                .build()

            PhoneAuthProvider.verifyPhoneNumber(options)
        }

        phoneBinding.btnVerifyCode.setOnClickListener {
            signInWithSMSCode()
        }

        mCallbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks(){
            override fun onVerificationCompleted(p0: PhoneAuthCredential) {
                TODO("Not yet implemented")
            }

            override fun onVerificationFailed(p0: FirebaseException) {
                TODO("Not yet implemented")
            }

            override fun onCodeSent(p0: String, p1: PhoneAuthProvider.ForceResendingToken) {
                super.onCodeSent(p0, p1)

                verificationCode = p0
            }
        }
    }

    private fun signInWithSMSCode(){

        val userEnteredCode = phoneBinding.etSmsCode.text.toString()

        val credential = PhoneAuthProvider.getCredential(verificationCode,userEnteredCode)

        signInWithPhoneAuthCredential(credential)
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential){

        auth.signInWithCredential(credential).addOnCompleteListener { task ->

            if (task.isSuccessful){

                val intent = Intent(this@PhoneActivity, MainActivity::class.java)
                startActivity(intent)
                finish()

            }else{
                Toast.makeText(applicationContext,"The code you entered is incorrect",Toast.LENGTH_SHORT).show()
            }
        }
    }
}