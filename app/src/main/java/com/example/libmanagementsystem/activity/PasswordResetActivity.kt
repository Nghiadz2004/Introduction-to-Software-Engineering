package com.example.libmanagementsystem.activity

import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.libmanagementsystem.R
import com.example.libmanagementsystem.dialog.LoadingDialog
import com.example.libmanagementsystem.repository.UserRepository
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import kotlinx.coroutines.launch

class PasswordResetActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private val userRepository = UserRepository()

    private lateinit var usernameField: EditText
    private lateinit var guidingMessage: TextView
    private lateinit var confirmButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)
        auth = Firebase.auth

        // Ánh xạ view
        usernameField = findViewById(R.id.pswdUsername)
        guidingMessage = findViewById(R.id.pswdGuide)
        confirmButton = findViewById(R.id.pswdConfirm)


        confirmButton.setOnClickListener {
            val username = usernameField.text.toString().trim()
            val loadingDialog = LoadingDialog(this)
            loadingDialog.show()

            // Kiểm tra ô trống
            if (username.isEmpty()) {
                loadingDialog.dismiss()
                guidingMessage.text = "Please fill in your username/email"
                guidingMessage.visibility = View.VISIBLE
                return@setOnClickListener
            }

            // Dùng coroutine cho xử lý bất đồng bộ
            lifecycleScope.launch {
                // Xác định email cần dùng
                val emailToUse: String? = if (Patterns.EMAIL_ADDRESS.matcher(username).matches()) {
                    username // Là email hợp lệ rồi
                } else {
                    // Là username, tìm người dùng
                    val existingUser = userRepository.getUserByUsername(username)
                    existingUser?.email // Lấy email nếu có
                }

                // Nếu không tìm được email
                if (emailToUse.isNullOrEmpty()) {
                    loadingDialog.dismiss()
                    guidingMessage.text = "No account is linked with this username/email"
                    guidingMessage.visibility = View.VISIBLE
                    return@launch
                }

                // Gửi email reset mật khẩu
                Firebase.auth.sendPasswordResetEmail(emailToUse)
                    .addOnCompleteListener { task ->
                        loadingDialog.dismiss()
                        if (task.isSuccessful) {
                            guidingMessage.text = "Password reset email sent. Please check your inbox."
                        } else {
                            guidingMessage.text = "Failed to send password reset email. Please try again."
                        }
                        guidingMessage.visibility = View.VISIBLE
                    }
            }
        }

    }
}