package com.example.librarymanagementsystem

import com.example.librarymanagementsystem.activity.LoginActivity
import com.example.librarymanagementsystem.activity.RegisterActivity
import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.librarymanagementsystem.activity.HomeActivity
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth

class MainActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        auth = Firebase.auth

        val currentUser = auth.currentUser

        //startActivity(Intent(this@MainActivity, LoginActivity::class.java))
        startActivity(Intent(this@MainActivity, LoginActivity::class.java))
        finish()
    }
}