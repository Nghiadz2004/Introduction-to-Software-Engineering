package com.example.librarymanagementsystem.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.librarymanagementsystem.R
import com.example.librarymanagementsystem.activity.LoginActivity
import com.example.librarymanagementsystem.repository.UserRepository
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.launch

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
    // User
    private lateinit var userID: String
    // Repository
    private val userRepository = UserRepository()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    companion object {
        fun newInstance(userID: String): ProfileFragment {
            val fragment = ProfileFragment()
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
        // Card overlay register button
        registerBtn = view.findViewById(R.id.registerBtn)
        // Buttons
        editProfileBtn = view.findViewById(R.id.editProfileBtn)
        logoutBtn = view.findViewById(R.id.logoutBtn)

        // Load data from database
        lifecycleScope.launch {
            val user = userRepository.getUserById(userID)

            user?.let {
                usernameTV.text = it.username
                profileEmailTV.text = it.email

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

        // Listen information return when librarian accept the register card request
        parentFragmentManager.setFragmentResultListener(
            "readerCardRequestKey", viewLifecycleOwner
        ) { _, bundle ->
            val fullname = bundle.getString("readerCardFullName")
            val email = bundle.getString("readerCardEmail")

            // Update UI
            fullNameTV.text = fullname
            emailTV.text = email
        }

        parentFragmentManager.setFragmentResultListener(
            "profileRequestKey", viewLifecycleOwner
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
            FirebaseAuth.getInstance().signOut()

            // Return to main page
            val intent = Intent(requireContext(), LoginActivity::class.java)
            startActivity(intent)
            Firebase.auth.signOut()

            activity?.finish()
        }

        editProfileBtn.setOnClickListener {
            val editFragment = EditProfileFragment.newInstance(userID)
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_profile, editFragment)
                .addToBackStack(null)
                .commit()
        }

        registerBtn.setOnClickListener {
            // Go to register reader card page
            val registerFragment = RegisterReaderCardFragment()
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_profile, registerFragment)
                .addToBackStack(null)
                .commit()
        }
    }
}