package com.zabava.firebase

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.zabava.firebase.databinding.ActivityUpdateUserBinding

class UpdateUserActivity : AppCompatActivity() {

    private lateinit var updateBinding : ActivityUpdateUserBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        updateBinding = ActivityUpdateUserBinding.inflate(layoutInflater)
        val view = updateBinding.root
        setContentView(view)
    }
}