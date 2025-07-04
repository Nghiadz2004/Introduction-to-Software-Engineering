package com.example.librarymanagementsystem

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.librarymanagementsystem.activity.HomeActivity
import com.example.librarymanagementsystem.activity.ActivityLibrarian
import com.example.librarymanagementsystem.activity.ActivityStorekeeper
import com.example.librarymanagementsystem.activity.WelcomeActivity
import com.example.librarymanagementsystem.repository.UserRepository
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

        // Sử dụng onAuthStateChangedListener để đảm bảo Firebase Auth đã sẵn sàng
        val currentUser = auth.currentUser
        Log.d("Current user", "${currentUser.toString()} ${rememberMe}")
        if ( currentUser == null || !rememberMe) {
            auth.signOut()
            startActivity(Intent(this@MainActivity, WelcomeActivity::class.java))
            finish()
        } else {
                // Nếu có người dùng đăng nhập và "Remember Me" được chọn
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
                        startActivity(Intent(this@MainActivity, ActivityStorekeeper::class.java))
                    }
                    finish()
                }
            }
    }
}