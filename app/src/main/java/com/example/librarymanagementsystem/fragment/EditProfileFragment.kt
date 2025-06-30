package com.example.librarymanagementsystem.fragment

import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.librarymanagementsystem.R
import com.example.librarymanagementsystem.repository.UserRepository
import com.example.librarymanagementsystem.service.ImageManager
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
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
    ): View = inflater.inflate(R.layout.fragment_edit_profile, container, false)

    companion object {
        fun newInstance(userID: String): EditProfileFragment {
            val fragment = EditProfileFragment()
            fragment.arguments = Bundle().apply { putString("USER_ID", userID) }
            return fragment
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        userID = Firebase.auth.currentUser?.uid!!
        // Bind UI elements
        avatarIV = view.findViewById(R.id.avatarIV)
        editAvatarBtn = view.findViewById(R.id.editAvatarBtn)
        userNameET = view.findViewById(R.id.userNameET)
        emailET = view.findViewById(R.id.emailET)
        passwordET = view.findViewById(R.id.passwordET)
        confirmPasswordET = view.findViewById(R.id.confirmPasswordET)
        cancelBtn = view.findViewById(R.id.cancelBtn)
        saveBtn = view.findViewById(R.id.saveBtn)

        // Load user data
        viewLifecycleOwner.lifecycleScope.launch {
            val user = userRepository.getUserById(userID)
            user?.let {
                userNameET.setText(it.username)
                emailET.setText(it.email)
                if (!it.avatar.isNullOrEmpty()) {
                    Glide.with(avatarIV.context)
                        .load(it.avatar)
                        .placeholder(R.drawable.ic_launcher_background)
                        .error(R.drawable.ic_launcher_background)
                        .into(avatarIV)
                } else {
                    avatarIV.setImageResource(R.drawable.ic_launcher_background)
                }

            }
        }

        // Avatar picker
        imagePickerLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                avatarIV.setImageURI(it)
                selectedAvatarUri = it
            }
        }

        editAvatarBtn.setOnClickListener {
            imagePickerLauncher.launch("image/*")
        }

        cancelBtn.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        saveBtn.setOnClickListener {
            viewLifecycleOwner.lifecycleScope.launch {
                val user = userRepository.getUserById(userID)
                if (user == null) {
                    Toast.makeText(requireContext(), "User not found", Toast.LENGTH_SHORT).show()
                    return@launch
                }

                // Update text fields
                user.username = userNameET.text.toString()
                user.email = emailET.text.toString()

                // Password
                val newPassword = passwordET.text.toString()
                val confirmPassword = confirmPasswordET.text.toString()

                if (newPassword.isNotBlank()) {
                    if (newPassword.length < MIN_PASSWORD_LENGTH) {
                        Toast.makeText(requireContext(), "Password must be at least $MIN_PASSWORD_LENGTH characters", Toast.LENGTH_SHORT).show()
                        return@launch
                    }
                    if (newPassword != confirmPassword) {
                        Toast.makeText(requireContext(), "Passwords do not match", Toast.LENGTH_SHORT).show()
                        return@launch
                    }

                    try {
                        Firebase.auth.currentUser?.updatePassword(newPassword)?.await()
                        Toast.makeText(requireContext(), "Password updated", Toast.LENGTH_SHORT).show()
                    } catch (e: Exception) {
                        Toast.makeText(requireContext(), "Failed to update password: ${e.message}", Toast.LENGTH_SHORT).show()
                        return@launch
                    }
                }

                // Avatar upload
                if (selectedAvatarUri != null) {
                    try {
                        val imageManager = ImageManager(requireContext())

                        // Upload lên Cloudinary bằng ImageManager
                        val imageUrl = imageManager.uploadImageToCloudinary(selectedAvatarUri!!)

                        // Cập nhật trường avatar
                        user.avatar = imageUrl

                        FirebaseFirestore.getInstance()
                            .collection("users")
                            .document(userID)
                            .set(user)
                            .await()

                        Toast.makeText(requireContext(), "Cập nhật thành công!", Toast.LENGTH_SHORT).show()

                    } catch (e: Exception) {
                        Log.e("UploadAvatar", "Lỗi khi cập nhật avatar", e)
                        Toast.makeText(requireContext(), "Lỗi: ${e.message}", Toast.LENGTH_LONG).show()
                    }
                } else {
                    // Không có avatar mới, chỉ cập nhật thông tin khác
                    FirebaseFirestore.getInstance()
                        .collection("users")
                        .document(userID)
                        .set(user)
                        .await()
                    Toast.makeText(requireContext(), "Cập nhật thành công (không đổi ảnh)!", Toast.LENGTH_SHORT).show()
                }

                // Update DB
                userRepository.updateUser(user)

                // Return result
                val result = Bundle().apply {
                    putString("profileUserName", user.username)
                    putString("profileEmail", user.email)
                    putString("profileAvatar", user.avatar)
                }
                parentFragmentManager.setFragmentResult(EDIT_REQUEST, result)
                parentFragmentManager.popBackStack()
            }
        }
    }
}