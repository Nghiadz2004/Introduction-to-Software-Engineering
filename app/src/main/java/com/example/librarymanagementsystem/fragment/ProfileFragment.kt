package com.example.librarymanagementsystem.fragment

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.example.librarymanagementsystem.R

class ProfileFragment : Fragment() {
    private lateinit var avatarIV: ImageView
    private lateinit var logoutBtn: Button
    private lateinit var fullNameTV: TextView
    private lateinit var usernameTV: TextView
    private lateinit var emailTV: TextView
    private lateinit var birthdayTV: TextView
    private lateinit var editProfileTV: TextView
    private lateinit var typeTV: TextView
    private lateinit var dueDateTV: TextView
    private lateinit var statusTV: TextView
    private lateinit var registerBtn: Button

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
        avatarIV = view.findViewById(R.id.avatarIV)
        logoutBtn = view.findViewById(R.id.logoutBtn)
        fullNameTV = view.findViewById(R.id.fullNameTV)
        usernameTV = view.findViewById(R.id.usernameTV)
        emailTV = view.findViewById(R.id.emailTV)
        birthdayTV = view.findViewById(R.id.birthdayTV)
        editProfileTV = view.findViewById(R.id.editProfileBtn)
        typeTV = view.findViewById(R.id.typeTV)
        dueDateTV = view.findViewById(R.id.dueDateTV)
        statusTV = view.findViewById(R.id.statusTV)
        registerBtn = view.findViewById(R.id.registerTV)

        // Load data from database
        avatarIV.setImageURI()
        usernameTV.text =
        emailTV.text =
        birthdayTV.text =
        typeTV.text =
        dueDateTV.text =
        statusTV.text =

        // Lắng nghe kết quả trả về từ RegisterReaderCardFragment
        parentFragmentManager.setFragmentResultListener(
            "readerCardRequestKey", viewLifecycleOwner
        ) { key, bundle ->
            val type = bundle.getString("readerCardType")
            val due_date = bundle.getString("readerCardDueDate")
            val status = bundle.getString("readeCardStatus")

            // Cập nhật UI
            typeTV.text = type
            dueDateTV.text = due_date
            statusTV.text = status
        }

        // Button click
        logoutBtn.setOnClickListener {
            // Logout of firebase

            // Return to main page
            val intent = Intent(requireContext(), ::class.java)
            startActivity(intent)

            activity?.finish()
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