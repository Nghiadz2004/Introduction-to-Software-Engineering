package com.example.libmanagementsystem.activity


import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.libmanagementsystem.R
import com.example.libmanagementsystem.dialog.LoadingDialog
import com.example.libmanagementsystem.model.User
import com.example.libmanagementsystem.model.UserStatus
import com.example.libmanagementsystem.repository.FavoriteRepository
import com.example.libmanagementsystem.repository.UserRepository
import com.google.firebase.Firebase
import java.text.SimpleDateFormat
import java.util.Locale
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.auth.userProfileChangeRequest
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.Calendar

class RegisterActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private val userRepository = UserRepository()
    private val favRepository = FavoriteRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.fragment_register)

        val fullnameField = findViewById<EditText>(R.id.rgFullname)
        val usernameField = findViewById<EditText>(R.id.rgUsername)
        val passwordField = findViewById<EditText>(R.id.rgPassword)
        val emailField = findViewById<EditText>(R.id.rgEmail)
        val birthdayField = findViewById<EditText>(R.id.rgBirthday)
        val submitButton = findViewById<Button>(R.id.rgSubmit)
        val errorMessage = findViewById<TextView>(R.id.rgErrorMessage)

        // errorMessage.visibility = View.GONE // Ẩn thông báo lỗi ban đầu

        birthdayField.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            val datePicker = DatePickerDialog(this,
                { _, selectedYear, selectedMonth, selectedDay ->
                    // Gán ngày sinh đã chọn vào EditText
                    val selectedDate = Calendar.getInstance()
                    selectedDate.set(selectedYear, selectedMonth, selectedDay)

                    val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                    val dateStr = sdf.format(selectedDate.time)

                    birthdayField.setText(dateStr)
                },
                year, month, day
            )

            // Tuỳ chọn: Giới hạn ngày chọn (ví dụ không cho chọn tương lai)
            datePicker.datePicker.maxDate = System.currentTimeMillis()

            datePicker.show()
        }

        submitButton.setOnClickListener {
            auth = Firebase.auth
            val fullname = fullnameField.text.toString().trim()
            val username = usernameField.text.toString().trim()
            val password = passwordField.text.toString().trim()
            val email = emailField.text.toString().trim()
            val birthdayStr = birthdayField.text.toString().trim()
            val loadingDialog = LoadingDialog(this)


            // Bước 1: Kiểm tra trống
            if (fullname.isEmpty() || username.isEmpty() || password.isEmpty() || email.isEmpty() || birthdayStr.isEmpty()) {
                errorMessage.text = "Error: Please fill all the required fields"
                errorMessage.visibility = View.VISIBLE
                return@setOnClickListener
            }

            val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val birthdayDate = try {
                sdf.parse(birthdayStr)
            } catch (e: Exception) {
                errorMessage.text = "Error: Invalid birthday format"
                errorMessage.visibility = View.VISIBLE
                return@setOnClickListener
            }

            if (password.length < 6) {
                errorMessage.text = "Error: Password must be at least 6 characters"
                errorMessage.visibility = View.VISIBLE
                return@setOnClickListener
            }

            // Bước 2.2: Kiểm tra định dạng email (đơn giản)
            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                errorMessage.text = "Error: Invalid email format"
                errorMessage.visibility = View.VISIBLE
                return@setOnClickListener
            }

            loadingDialog.show()
            lifecycleScope.launch {
                try {
                    val existingUser = userRepository.getUserByUsername(username)
                    if (existingUser != null) {
                        loadingDialog.dismiss()
                        errorMessage.text = "Error: Username already exists"
                        errorMessage.visibility = View.VISIBLE
                        return@launch
                    }

                    val result = auth.createUserWithEmailAndPassword(email, password).await()
                    val user = result.user ?: throw Exception("Error: Cannot create the account. Please try again later.")

                    // Cập nhật displayName (nếu cần)
                    val profileUpdates = userProfileChangeRequest {
                        displayName = fullname
                    }
                    user.updateProfile(profileUpdates).await()

                    // Tạo User trong Firestore
                    val newUser = User(
                        id = user.uid,
                        username = username,
                        email = email,
                        roleId = "reader",
                        fullname = fullname,
                        birthday = birthdayDate!!,
                        status = UserStatus.ACTIVE.value
                    )
                    userRepository.createUser(newUser)

                    // Tạo Favorite nếu cần
                    favRepository.createFavorite(user.uid, emptyList()) // Không cần truyền list

                    // Thành công → chuyển sang login
                    startActivity(Intent(this@RegisterActivity, LoginActivity::class.java))
                    finish()
                } catch (e: Exception) {
                    errorMessage.text = "Error: ${e.message}"
                    errorMessage.visibility = View.VISIBLE
                } finally {
                    loadingDialog.dismiss()
                }
            }

        }

    }
}