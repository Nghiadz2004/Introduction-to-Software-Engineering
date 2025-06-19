package com.example.libmanagementsystem

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.libmanagementsystem.activity.HomeActivity
import com.example.libmanagementsystem.activity.ActivityLibrarian
import com.example.libmanagementsystem.activity.WelcomeActivity
import com.example.libmanagementsystem.repository.UserRepository
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        auth = Firebase.auth
        val sharedPref = getSharedPreferences("user_prefs", MODE_PRIVATE)
        val rememberMe = sharedPref.getBoolean("remember_me", false)

        val currentUser = auth.currentUser
        if ( currentUser == null || !rememberMe) {
            auth.signOut()
            startActivity(Intent(this@MainActivity, WelcomeActivity::class.java))
            finish()
        }
        else{
            val userRepository = UserRepository()
            lifecycleScope.launch {
                val userObject = userRepository.getUserById(currentUser.uid)
                if (userObject?.roleId == "reader") {
                    startActivity(Intent(this@MainActivity, HomeActivity::class.java))
                }
                else if (userObject?.roleId == "librarian") {
                    startActivity(Intent(this@MainActivity, ActivityLibrarian::class.java))
                }
                else{

                }
                finish()
            }
        }

    }
}