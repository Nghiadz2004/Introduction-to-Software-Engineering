package com.example.librarymanagementsystem.fragment

import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.librarymanagementsystem.R
import com.example.librarymanagementsystem.repository.UserRepository
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

private const val MIN_PASSWORD_LENGTH = 12
private const val EDIT_REQUEST = "editProfileRequestKey"

class EditProfileFragment : Fragment() {
    private lateinit var avatarIV: ImageView
    private lateinit var editAvatarBtn: ImageButton
    private lateinit var imagePickerLauncher: ActivityResultLauncher<String>

    private lateinit var userNameET: EditText
    private lateinit var emailET: EditText
    private lateinit var passwordET: EditText
    private lateinit var confirmPasswordET: EditText

    private lateinit var cancelBtn: Button
    private lateinit var saveBtn: Button

    private lateinit var userID: String

    private val userRepository = UserRepository()

    private var selectedAvatarUri: Uri? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_edit_profile, container, false)
    }

    companion object {
        fun newInstance(userID: String): EditProfileFragment {
            val fragment = EditProfileFragment()
            val args = Bundle()
            args.putString("USER_ID", userID)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        userID = arguments?.getString("USER_ID") ?: ""

        // Initial predefine variables
        avatarIV = view.findViewById(R.id.avatarIV)
        editAvatarBtn = view.findViewById(R.id.editAvatarBtn)
        userNameET = view.findViewById(R.id.userNameET)
        emailET = view.findViewById(R.id.emailET)
        passwordET = view.findViewById(R.id.passwordET)
        confirmPasswordET = view.findViewById(R.id.confirmPasswordET)

        cancelBtn = view.findViewById(R.id.cancelBtn)
        saveBtn = view.findViewById(R.id.saveBtn)

        lifecycleScope.launch {
            val user = userRepository.getUserById(userID)

            user?.let {
                userNameET.setText(it.username)
                emailET.setText(it.email)
                Glide.with(avatarIV)
                    .load(it.avatar)
                    .into(avatarIV)
            }
        }

        // Button click
        // Register the ActivityResultLauncher and handle edit button click
        imagePickerLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                avatarIV.setImageURI(it)
                selectedAvatarUri = it
            }
        }

        editAvatarBtn.setOnClickListener {
            imagePickerLauncher.launch("image/*")
        }

        // Handle submit button, Return reader card information to profile page
        // and send data to database and return to profile page
        saveBtn.setOnClickListener {
            lifecycleScope.launch {
                val existingUser = userRepository.getUserById(userID)

                if (existingUser != null) {
                    // Update text fields
                    existingUser.username = userNameET.text.toString()
                    existingUser.email = emailET.text.toString()

                    // Xử lý mật khẩu
                    val newPassword = passwordET.text.toString()
                    val confirmPassword = confirmPasswordET.text.toString()
                    if (newPassword.isNotBlank() && newPassword.length >= MIN_PASSWORD_LENGTH) {
                        if (newPassword == confirmPassword) {
                            try {
                                Firebase.auth.currentUser?.updatePassword(newPassword)
                                Toast.makeText(requireContext(), "Password updated", Toast.LENGTH_SHORT).show()
                            } catch (e: Exception) {
                                Toast.makeText(requireContext(), "Failed to update password", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }

                    // Xử lý avatar: upload lên Storage nếu có ảnh mới
                    if (selectedAvatarUri != null) {
                        val storageRef = FirebaseStorage.getInstance().reference
                        val avatarRef = storageRef.child("avatars/${userID}.jpg")

                        try {
                            val uploadTask = avatarRef.putFile(selectedAvatarUri!!)
                            val urlTask = uploadTask.continueWithTask { task ->
                                if (!task.isSuccessful) throw task.exception!!
                                avatarRef.downloadUrl
                            }.await()

                            existingUser.avatar = urlTask.toString() // Lấy URL ảnh sau khi upload
                        } catch (e: Exception) {
                            Toast.makeText(requireContext(), "Failed to upload avatar", Toast.LENGTH_SHORT).show()
                            Log.e("Upload", "Error uploading avatar", e)
                        }
                    }

                    // Cập nhật thông tin lên Firestore
                    userRepository.updateUser(existingUser)

                    // Trả kết quả về Fragment trước đó
                    val result = Bundle().apply {
                        putString("profileUserName", existingUser.username)
                        putString("profileEmail", existingUser.email)
                        putString("profileAvatar", existingUser.avatar)
                    }
                    parentFragmentManager.setFragmentResult(EDIT_REQUEST, result)
                    parentFragmentManager.popBackStack()
                } else {
                    Toast.makeText(requireContext(), "User not found", Toast.LENGTH_SHORT).show()
                }
            }
        }

        cancelBtn.setOnClickListener {

            parentFragmentManager.popBackStack() // Return to ProfileFragment
        }
    }
}