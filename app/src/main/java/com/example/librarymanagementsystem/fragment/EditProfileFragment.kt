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
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.launch

private const val MIN_PASSWORD_LENGTH = 12

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
        if (userID.isBlank()) {
            Toast.makeText(requireContext(), "Invalid user ID", Toast.LENGTH_LONG).show()
            return
        }

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

                it.avatar?.let { avatarPath ->
                    val storageRef = FirebaseStorage.getInstance().reference.child(avatarPath)
                    storageRef.downloadUrl.addOnSuccessListener { uri ->
                        Glide.with(avatarIV)
                            .load(uri)
                            .placeholder(R.drawable.ic_launcher_background)
                            .into(avatarIV)
                    }.addOnFailureListener { e ->
                        avatarIV.setImageResource(R.drawable.ic_launcher_background)
                        Log.e("ProfileFragment", "Failed to load avatar", e)
                    }
                } ?: run {
                    avatarIV.setImageResource(R.drawable.ic_launcher_background)
                }
            } ?: run {
                Toast.makeText(requireContext(), "User not found", Toast.LENGTH_LONG).show()
            }
        }

        // Button click
        // Register the ActivityResultLauncher and handle edit button click
        imagePickerLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                avatarIV.setImageURI(it)
                avatarIV.tag = it
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
                    // Update fields with new values from EditText
                    existingUser.username = userNameET.text.toString()
                    existingUser.email = emailET.text.toString()

                    // validate and compare passwords before setting
                    val newPassword = passwordET.text.toString()
                    val confirmPassword = confirmPasswordET.text.toString()
                    if (newPassword.isNotBlank() && newPassword.length >= MIN_PASSWORD_LENGTH) {
                        if (newPassword == confirmPassword) {
                            // Hash password before saving!
                            // existingUser.password = newPassword
                        }
                    }

                    // TODO: Upload avatar to Firebase Storage (if changed)

                } else {
                    Toast.makeText(requireContext(), "User not found", Toast.LENGTH_SHORT).show()
                }
            }

            // Return user information
            val result = Bundle().apply {
                putString("profileUserName", userNameET.text.toString())
                putString("profileEmail", emailET.text.toString())
            }

            parentFragmentManager.setFragmentResult("profileRequestKey", result)
            parentFragmentManager.popBackStack() // Return to ProfileFragment
        }

        cancelBtn.setOnClickListener {

            parentFragmentManager.popBackStack() // Return to ProfileFragment
        }
    }
}