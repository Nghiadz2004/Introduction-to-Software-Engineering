package com.example.librarymanagementsystem.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.librarymanagementsystem.R
import com.example.librarymanagementsystem.cache.FavoriteCache
import com.example.librarymanagementsystem.dialog.LoadingDialog
import com.example.librarymanagementsystem.repository.FavoriteRepository
import com.example.librarymanagementsystem.repository.UserRepository
import com.example.librarymanagementsystem.service.FavoriteManager
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class LoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private val userRepository = UserRepository()

    private lateinit var usernameField: EditText
    private lateinit var passwordField: EditText
    private lateinit var errorMessage: TextView
    private lateinit var loginButton: Button
    private lateinit var rememberMeCheckbox: CheckBox
    private lateinit var rememberMeLayout: LinearLayout
    private lateinit var forgotPasswordText: TextView
    private lateinit var registerText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        auth = Firebase.auth
        val sharedPref = getSharedPreferences("user_prefs", MODE_PRIVATE)

        // Ánh xạ view
        rememberMeLayout = findViewById<LinearLayout>(R.id.lgRememberMe)
        usernameField = findViewById(R.id.lgUsername)
        passwordField = findViewById(R.id.lgPassword)
        errorMessage = findViewById(R.id.lgErrorMessage)
        loginButton = findViewById(R.id.lgSubmit)
        rememberMeCheckbox = findViewById(R.id.rememberMeCheckbox)
        forgotPasswordText = findViewById(R.id.lgForgotPassword)
        registerText = findViewById(R.id.lgRegister)

        registerText.setOnClickListener {
            startActivity(Intent(this@LoginActivity, RegisterActivity::class.java))
        }

        forgotPasswordText.setOnClickListener {
            startActivity(Intent(this@LoginActivity, PasswordResetActivity::class.java))
        }

        rememberMeLayout.setOnClickListener {
            rememberMeCheckbox.isChecked = !rememberMeCheckbox.isChecked
        }

        // Xử lý sự kiện khi nhấn nút Login
        loginButton.setOnClickListener {
            val username = usernameField.text.toString().trim()
            val password = passwordField.text.toString().trim()
            val loadingDialog = LoadingDialog(this)
            loadingDialog.show()

            // Kiểm tra trống
            if (username.isEmpty() || password.isEmpty()){
                loadingDialog.dismiss()
                errorMessage.text = "Please fill all the required fields"
                errorMessage.visibility = View.VISIBLE
                return@setOnClickListener
            }

            // Kiểm tra định dạng mật khẩu
            if (password.length < 6) {
                loadingDialog.dismiss()
                errorMessage.text = "Password must be at least 6 characters"
                errorMessage.visibility = View.VISIBLE
                return@setOnClickListener
            }

            // Kiểm tra định dạng email (đơn giản)
            lifecycleScope.launch {
                val emailToUse = if (!Patterns.EMAIL_ADDRESS.matcher(username).matches()) {
                    val existingUser = userRepository.getUserByUsername(username)
                    if (existingUser?.email == null) {
                        loadingDialog.dismiss()
                        errorMessage.text = "Incorrect username or password"
                        errorMessage.visibility = View.VISIBLE
                        return@launch
                    } else {
                        existingUser.email
                    }
                } else {
                    username // it's already an email
                }

                try {
                    auth.signInWithEmailAndPassword(emailToUse!!, password).await()
                    sharedPref.edit().putBoolean("remember_me", rememberMeCheckbox.isChecked).apply()
                    errorMessage.text = "Login successfully! Please wait..."
                    errorMessage.visibility = View.VISIBLE
                    loadingDialog.dismiss()

                    val userRepository = UserRepository()
                    lifecycleScope.launch {
                        val userObject = userRepository.getUserByEmail(emailToUse)

                        if (userObject?.roleId == "reader") {
                            startActivity(Intent(this@LoginActivity, HomeActivity::class.java))
                        } else if (userObject?.roleId == "librarian") {
                            startActivity(Intent(this@LoginActivity, ActivityLibrarian::class.java))
                        } else if (userObject?.roleId == "storekeeper") {
                            startActivity(Intent(this@LoginActivity, ActivityStorekeeper::class.java))
                        }
                        finish()
                    }
                } catch (e: Exception) {
                    loadingDialog.dismiss()
                    errorMessage.text = "Incorrect username or password"
                    errorMessage.visibility = View.VISIBLE
                }
            }

        }
    }
}
