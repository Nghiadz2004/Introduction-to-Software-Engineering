package com.example.librarymanagementsystem.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.RadioGroup
import com.example.librarymanagementsystem.R
import androidx.activity.result.ActivityResultLauncher

class RegisterReaderCardFragment : Fragment() {
    private lateinit var fullNameET: EditText
    private lateinit var emailET: EditText
    private lateinit var birthdayET: EditText
    private lateinit var addressET: EditText
    private lateinit var typeRG: RadioGroup
    private lateinit var submitBtn: Button

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
        fullNameET = view.findViewById(R.id.fullnameET)
        emailET = view.findViewById(R.id.emailET)
        birthdayET = view.findViewById(R.id.birthdayET)
        addressET = view.findViewById(R.id.addressET)
        typeRG = view.findViewById(R.id.typeRG)
        submitBtn = view.findViewById(R.id.submitBtn)

        // TODO: Load data from database
//        avatarIV.setImageResource()
//        fullNameET.hint =
//        emailET.hint =
//        birthdayET.hint =
//        addressET.hint =
//        typeRG.isSelected =

        // Button click
        // Handle submit button, Return reader card information to profile page
        // and send data to database and return to profile page
        submitBtn.setOnClickListener {
            // TODO: Update to database

            // Return reader card information
//            val result = Bundle().apply {
//                putString("readerCardFullName", fullNameET.text.toString())
//                putString("readerCardEmail", emailET.text.toString())
//                putString("readerCardBirthday", birthdayET.text.toString())
//                putString("readerCardType", )
//                putString("readerCardDueDate", )
//            }

            parentFragmentManager.popBackStack() // Return to ProfileFragment
        }
    }
}