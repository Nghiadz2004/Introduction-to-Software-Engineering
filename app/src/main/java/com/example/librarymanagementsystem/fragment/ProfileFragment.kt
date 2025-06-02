package com.example.librarymanagementsystem.fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.example.librarymanagementsystem.R

class ProfileFragment : Fragment() {
    // Profile information
    private lateinit var avatarIV: ImageView
    private lateinit var usernameTV: TextView
    private lateinit var profileEmailTV: TextView
    // Card information
    private lateinit var fullNameTV: TextView
    private lateinit var emailTV: TextView
    private lateinit var birthdayTV: TextView
    private lateinit var addressTV: TextView
    private lateinit var dueDateTV: TextView
    // Card overlay register button
    private lateinit var registerBtn: Button
    // Buttons
    private lateinit var editProfileBtn: Button
    private lateinit var logoutBtn: Button
    // Status
    private lateinit var statusIV: ImageView
    private lateinit var statusTV: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initial predefine variables
        // Profile information
        avatarIV = view.findViewById(R.id.avatarIV)
        fullNameTV = view.findViewById(R.id.fullNameTV)
        profileEmailTV = view.findViewById(R.id.profileEmailTV)
        // Card information
        usernameTV = view.findViewById(R.id.usernameTV)
        birthdayTV = view.findViewById(R.id.birthdayTV)
        statusTV = view.findViewById(R.id.statusTV)
        statusIV = view.findViewById(R.id.statusIV)
        dueDateTV = view.findViewById(R.id.dueDateTV)
//        typeTV = view.findViewById(R.id.typeTV)
        // Card overlay register button
        registerBtn = view.findViewById(R.id.registerBtn)
        // Buttons
        editProfileBtn = view.findViewById(R.id.editProfileBtn)
        logoutBtn = view.findViewById(R.id.logoutBtn)

        // Load data from database
//        avatarIV.setImageURI()
//        usernameTV.text =
//        emailTV.text =
//        birthdayTV.text =
//        typeTV.text =
//        dueDateTV.text =
//        statusTV.text =

       // Listen information return when librarian accept the register card request
//        parentFragmentManager.setFragmentResultListener(
//            "readerCardRequestKey", viewLifecycleOwner
//        ) { key, bundle ->
//            val fullname = bundle.getString("readerCardFullName")
//            val email = bundle.getString("readerCardEmail")
//            val birthday = bundle.getString("readerCardBirthday")
//            val address = bundle.getString("readerCardAddress")
//            val dueDate = bundle.getString("readerDueDate")
//            val type = bundle.getString("readerCardType")
//
//            // Update UI
//            fullNameTV.text = fullname
//            emailTV.text = email
//            birthdayTV.text = birthday
//            addressTV.text = address
//            dueDateTV.text = dueDate
//            type.text = type
//            statusIV.setColorFilter(ContextCompat.getColor(view.context, R.color.green))
//            statusTV.text = "Activate"
//        }

        parentFragmentManager.setFragmentResultListener(
            "editProfileRequestKey", viewLifecycleOwner
        ) { key, bundle ->
            val fullname = bundle.getString("readerCardFullName")
            val email = bundle.getString("readerCardEmail")

            // Update UI
            fullNameTV.text = fullname
            emailTV.text = email
        }

        // Button click
        logoutBtn.setOnClickListener {
            // Logout of firebase

            // Return to main page
            val intent = Intent(requireContext(), ::class.java)
            startActivity(intent)

            activity?.finish()
        }

        editProfileBtn.setOnClickListener {
            val fragment = EditProfileFragment()
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_profile, fragment)
                .addToBackStack(null)
                .commit()
        }

        registerBtn.setOnClickListener {
            // Go to register reader card page
            val fragment = RegisterReaderCardFragment()
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_profile, fragment)
                .addToBackStack(null)
                .commit()
        }
    }
}