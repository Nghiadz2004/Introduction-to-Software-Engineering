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
        auth.addAuthStateListener { firebaseAuth ->
            val currentUser = firebaseAuth.currentUser
            Log.d("MainActivity", "Current user in listener: $currentUser")

            if (currentUser == null || !rememberMe) {
                // Nếu không có người dùng đăng nhập hoặc "Remember Me" không được chọn,
                // đảm bảo đăng xuất (nếu có user nào đó còn sót lại) và chuyển về WelcomeActivity
                if (currentUser != null) { // Chỉ signOut nếu có currentUser hiện tại
                    firebaseAuth.signOut()
                }
                startActivity(Intent(this@MainActivity, WelcomeActivity::class.java))
                finish()
            } else {
                // Nếu có người dùng đăng nhập và "Remember Me" được chọn
                val userRepository = UserRepository()
                lifecycleScope.launch {
                    val userObject = userRepository.getUserById(currentUser.uid)
                    when (userObject?.roleId) {
                        "reader" -> {
                            startActivity(Intent(this@MainActivity, HomeActivity::class.java))
                        }
                        "librarian" -> {
                            startActivity(Intent(this@MainActivity, ActivityLibrarian::class.java))
                        }
                        "storekeeper" -> {
                            startActivity(Intent(this@MainActivity, ActivityStorekeeper::class.java))
                        }
                        else -> {
                            // Xử lý trường hợp roleId không xác định hoặc null
                            // Có thể chuyển về LoginActivity hoặc WelcomeActivity
                            firebaseAuth.signOut() // Đăng xuất nếu không xác định được vai trò
                            startActivity(Intent(this@MainActivity, WelcomeActivity::class.java))
                        }
                    }
                    finish()
                }
            }
        }
    }
}