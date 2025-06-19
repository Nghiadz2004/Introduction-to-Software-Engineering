package com.example.libmanagementsystem.fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.libmanagementsystem.R
import com.example.libmanagementsystem.activity.LoginActivity
import com.example.libmanagementsystem.dialog.LoadingDialog
import com.example.libmanagementsystem.repository.UserRepository
import com.example.libmanagementsystem.service.LibraryCardManager
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private const val EDIT_REQUEST = "editProfileRequestKey"

class ProfileFragment : Fragment() {
    private lateinit var auth: FirebaseAuth
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
    private lateinit var wrapper: FrameLayout
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
    // Dialog
    private lateinit var loadingDialog: LoadingDialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        loadingDialog = LoadingDialog(requireContext())
        auth = Firebase.auth
        userID = auth.currentUser!!.uid
        // Initial predefine variables
        // Profile information
        avatarIV = view.findViewById(R.id.avatarIV)
        fullNameTV = view.findViewById(R.id.fullNameTV)
        profileEmailTV = view.findViewById(R.id.profileEmailTV)
        // Card information
        wrapper = view.findViewById(R.id.registerFL)
        userNameTV = view.findViewById(R.id.userNameTV)
        birthdayTV = view.findViewById(R.id.birthdayTV)
        emailTV = view.findViewById(R.id.emailTV)
        addressTV = view.findViewById(R.id.addressTV)
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

        loadProfile(userID)
        handleProfileButton()

        return view
    }

    private fun formatDate(date: Date): String {
        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        return sdf.format(date)
    }

    private fun loadProfile(userID: String) {
        // Load data from database
        viewLifecycleOwner.lifecycleScope.launch {
            loadingDialog.show()

            try {
                listenReturnInformation()
                val defaultDrawable = ContextCompat.getDrawable(requireContext(), R.drawable.ic_launcher_background)

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
                    wrapper.visibility = View.GONE
                    fullNameTV.text = libraryCard.fullName
                    emailTV.text = libraryCard.email
                    addressTV.text = libraryCard.address
                    typeTV.text = libraryCard.type
                    birthdayTV.text = formatDate(libraryCard.birthday)
                    dueDateTV.text = formatDate(libraryCardManager.getDueDate(libraryCard.createdAt))
                    statusIV.setColorFilter(ContextCompat.getColor(requireContext(), R.color.green))
                    statusTV.text = libraryCard.status
                }
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Error loading profile: ${e.message}", Toast.LENGTH_LONG).show()
            } finally {
                loadingDialog.dismiss()
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