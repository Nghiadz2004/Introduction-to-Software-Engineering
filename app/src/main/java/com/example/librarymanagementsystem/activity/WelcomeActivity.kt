package com.example.librarymanagementsystem.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.librarymanagementsystem.R

class WelcomeActivity : AppCompatActivity() {
    private lateinit var loginByUsername: Button
    private lateinit var loginByGoogle: Button
    private lateinit var register: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)

        loginByUsername = findViewById(R.id.loginByUsername)
        loginByGoogle = findViewById(R.id.loginByGoogle)
        register = findViewById(R.id.register)
        Log.d("WELCOME_ACTIVITY", "onCreate called")
        loginByUsername.setOnClickListener {
            startActivity(Intent(this@WelcomeActivity, LoginActivity::class.java))
        }

        loginByGoogle.setOnClickListener {
            // TODO: Thực hiện đăng nhập bằng Facebook hoặc Google (ở đây ghi là "Google")

        }

        register.setOnClickListener {
            startActivity(Intent(this@WelcomeActivity, RegisterActivity::class.java))
        }
    }
}