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
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.librarymanagementsystem.R
import com.example.librarymanagementsystem.activity.LoginActivity
import com.example.librarymanagementsystem.repository.UserRepository
import com.example.librarymanagementsystem.service.LibraryCardManager
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private const val EDIT_REQUEST = "editProfileRequestKey"

class ProfileFragment : Fragment() {
    // Profile information
    private lateinit var avatarIV: ImageView
    private lateinit var userNameTV: TextView
    private lateinit var profileEmailTV: TextView
    // Card information
    private lateinit var fullNameTV: TextView
    private lateinit var emailTV: TextView
    private lateinit var addressTV: TextView
    private lateinit var typeTV: TextView
    private lateinit var birthdayTV: TextView
    private lateinit var dueDateTV: TextView
    private lateinit var statusIV: ImageView
    private lateinit var statusTV: TextView
    // Card overlay register button
    private lateinit var registerBtn: Button
    // Buttons
    private lateinit var editProfileBtn: Button
    private lateinit var logoutBtn: Button
    // User
    private lateinit var userID: String
    // Repository
    private val userRepository = UserRepository()
    // Manager
    private val libraryCardManager = LibraryCardManager()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        // Initial predefine variables
        // Profile information
        avatarIV = view.findViewById(R.id.avatarIV)
        fullNameTV = view.findViewById(R.id.fullNameTV)
        profileEmailTV = view.findViewById(R.id.profileEmailTV)
        // Card information
        userNameTV = view.findViewById(R.id.userNameTV)
        birthdayTV = view.findViewById(R.id.birthdayTV)
        emailTV = view.findViewById(R.id.emailTV)
        typeTV = view.findViewById(R.id.typeTV)
        birthdayTV = view.findViewById(R.id.birthdayTV)
        dueDateTV = view.findViewById(R.id.dueDateTV)
        statusIV = view.findViewById(R.id.statusIV)
        statusTV = view.findViewById(R.id.statusTV)
        // Card overlay register button
        registerBtn = view.findViewById(R.id.registerBtn)
        // Buttons
        editProfileBtn = view.findViewById(R.id.editProfileBtn)
        logoutBtn = view.findViewById(R.id.logoutBtn)

        userID = arguments?.getString("USER_ID") ?: ""

        loadProfile(userID)
        handleProfileButton()

        return view
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

    private fun formatDate(date: Date): String {
        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        return sdf.format(date)
    }

    private fun loadProfile(userID: String) {
        // Load data from database
        viewLifecycleOwner.lifecycleScope.launch {
            // If user edit profile, use the return information instead of calling Firestore API to optimize speed
            listenReturnInformation()
            val defaultDrawable = ContextCompat.getDrawable(requireContext(), R.drawable.ic_launcher_background)

            // If user didnt edit profile, use information from Firebase.
            if (userNameTV.text.isBlank() && emailTV.text.isBlank() && avatarIV.drawable != defaultDrawable) {
                val user = userRepository.getUserById(userID)

                user?.let {
                    userNameTV.text = it.username
                    profileEmailTV.text = it.email
                    Glide.with(avatarIV)
                        .load(it.avatar)
                        .placeholder(R.drawable.ic_launcher_background)
                        .into(avatarIV)
                } ?: run {
                    Toast.makeText(requireContext(), "User not found", Toast.LENGTH_LONG).show()
                }
            }

            val libraryCard = libraryCardManager.getCurrentLibraryCard(userID)
            if (libraryCard != null) {
                fullNameTV.text = libraryCard.fullName
                emailTV.text = libraryCard.email
                addressTV.text = libraryCard.address
                typeTV.text = libraryCard.type
                birthdayTV.text = formatDate(libraryCard.birthday)
                libraryCard.createdAt.let {
                    dueDateTV.text = formatDate(libraryCardManager.getDueDate(it))
                }
                statusIV.setColorFilter(ContextCompat.getColor(requireContext(), R.color.green))
                statusTV.text = libraryCard.status
            }
        }
    }

    private fun handleProfileButton() {
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
                .replace(R.id.fragment_container, editFragment)
                .addToBackStack(null)
                .commit()
        }

        registerBtn.setOnClickListener {
            // Go to register reader card page
            val registerFragment = RegisterReaderCardFragment.newInstance(userID)
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, registerFragment)
                .addToBackStack(null)
                .commit()
        }
    }

    private fun listenReturnInformation() {
        parentFragmentManager.setFragmentResultListener(
            EDIT_REQUEST, viewLifecycleOwner
        ) { key, bundle ->
            val userName = bundle.getString("profileUserName")
            val email = bundle.getString("profileEmail")
            val avatar = bundle.getString("profileAvatar")

            // Update UI
            userNameTV.text = userName
            emailTV.text = email
            Glide.with(avatarIV)
                .load(avatar)
                .placeholder(R.drawable.ic_launcher_background)
                .into(avatarIV)
        }
    }
}