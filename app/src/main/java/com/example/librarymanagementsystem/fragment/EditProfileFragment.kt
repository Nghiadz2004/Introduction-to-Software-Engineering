package com.example.librarymanagementsystem.fragment

import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.example.librarymanagementsystem.R

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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_edit_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initial predefine variables
        avatarIV = view.findViewById(R.id.avatarIV)
        editAvatarBtn = view.findViewById(R.id.editAvatarBtn)

        userNameET = view.findViewById(R.id.userNameET)
        emailET = view.findViewById(R.id.emailET)
        passwordET = view.findViewById(R.id.passwordET)
        confirmPasswordET = view.findViewById(R.id.confirmPasswordET)

        cancelBtn = view.findViewById(R.id.cancelBtn)
        saveBtn = view.findViewById(R.id.saveBtn)

        // TODO: Load data from database
//        avatarIV.setImageResource()

        // Button click
        // Register the ActivityResultLauncher and handle edit button click
        imagePickerLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                avatarIV.setImageURI(it)
            }
        }
        editAvatarBtn.setOnClickListener {
            imagePickerLauncher.launch("image/*")
        }

        // Handle submit button, Return reader card information to profile page
        // and send data to database and return to profile page
        saveBtn.setOnClickListener {
            // TODO: Update to database

            // Return reader card information
            val result = Bundle().apply {
                putString("profileUserName", userNameET.text.toString())
                putString("profileEmail", emailET.text.toString())
            }
            parentFragmentManager.setFragmentResult("editProfileRequestKey", result)
            parentFragmentManager.popBackStack() // Return to ProfileFragment
        }

        cancelBtn.setOnClickListener {
            parentFragmentManager.popBackStack() // Return to ProfileFragment
        }
    }
}