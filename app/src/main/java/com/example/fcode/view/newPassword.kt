package com.example.fcode.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.fcode.R
import com.example.fcode.databinding.ActivityNewPasswordBinding

class newPassword : AppCompatActivity() {

    private lateinit var binding: ActivityNewPasswordBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewPasswordBinding.inflate(layoutInflater)
        setContentView(R.layout.activity_new_password)
    }
}