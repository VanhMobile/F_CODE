package com.example.fcode.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.content.ContextCompat
import com.example.fcode.R
import com.example.fcode.databinding.ActivityForgotPasswordBinding
import com.example.fcode.databinding.ActivitySignUpBinding

class forgotPassword : AppCompatActivity() {
    private lateinit var binding:ActivityForgotPasswordBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        window.statusBarColor = ContextCompat.getColor(this, R.color.status_bar_color)
        binding = ActivityForgotPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnResetPassword.setOnClickListener {
            startActivity(Intent(this@forgotPassword,otpConfirm::class.java))
        }
    }
}