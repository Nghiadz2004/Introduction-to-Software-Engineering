package com.example.librarymanagementsystem.fragment

import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.RadioGroup
import com.example.librarymanagementsystem.R
import androidx.activity.result.contract.ActivityResultContracts
import android.net.Uri
import androidx.activity.result.ActivityResultLauncher

class RegisterReaderCardFragment : Fragment() {
    private lateinit var avatarIV: ImageView
    private lateinit var editAvatarBtn: ImageButton
    private lateinit var fullNameET: EditText
    private lateinit var emailET: EditText
    private lateinit var birthdayET: EditText
    private lateinit var addressET: EditText
    private lateinit var typeRG: RadioGroup
    private lateinit var submitBtn: Button

    private lateinit var imagePickerLauncher: ActivityResultLauncher<String>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_register_reader_card, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initial predefine variables
        avatarIV = view.findViewById(R.id.avatarIV)
        editAvatarBtn = view.findViewById(R.id.editAvatarBtn)
        fullNameET = view.findViewById(R.id.fullnameET)
        emailET = view.findViewById(R.id.emailET)
        birthdayET = view.findViewById(R.id.birthdayET)
        addressET = view.findViewById(R.id.addressET)
        typeRG = view.findViewById(R.id.typeRG)
        submitBtn = view.findViewById(R.id.submitBtn)

        // Load data from database
//        avatarIV.setImageResource()
//        fullNameET.hint
//        emailET.hint
//        birthdayET.hint
//        addressET.hint
//        typeRG.isSelected

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
        submitBtn.setOnClickListener {
            // Update to database

            // Return reader card information
            val result = Bundle().apply {
                putString("readerCardType", )
                putString("readerCardDueDate", )
            }
            parentFragmentManager.setFragmentResult("readerCardRequestKey", result)
            parentFragmentManager.popBackStack() // Return to ProfileFragment
        }
    }
}