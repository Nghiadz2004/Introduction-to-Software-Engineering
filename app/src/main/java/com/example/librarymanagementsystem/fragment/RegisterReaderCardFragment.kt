package com.example.librarymanagementsystem.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.RadioGroup
import android.widget.Toast
import com.example.librarymanagementsystem.R
import androidx.activity.result.ActivityResultLauncher
import androidx.lifecycle.lifecycleScope
import com.example.librarymanagementsystem.model.CardRequest
import com.example.librarymanagementsystem.model.RequestStatus
import com.example.librarymanagementsystem.repository.CardRequestRepository
import com.example.librarymanagementsystem.repository.UserRepository
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class RegisterReaderCardFragment : Fragment() {
    // Fill out form
    private lateinit var fullNameET: EditText
    private lateinit var emailET: EditText
    private lateinit var birthdayET: EditText
    private lateinit var addressET: EditText
    private lateinit var typeRG: RadioGroup

    // Button
    private lateinit var submitBtn: Button
    private lateinit var returnBtn: ImageButton

    private val cardRequestRepository = CardRequestRepository()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_register_reader_card, container, false)

        val userID = Firebase.auth.currentUser!!.uid

        fullNameET = view.findViewById(R.id.fullnameET)
        emailET = view.findViewById(R.id.emailET)
        birthdayET = view.findViewById(R.id.birthdayET)
        addressET = view.findViewById(R.id.addressET)
        typeRG = view.findViewById(R.id.typeRG)
        submitBtn = view.findViewById(R.id.submitBtn)
        returnBtn = view.findViewById(R.id.returnBtn)

        loadRegisterReaderCard(userID)

        return view
    }

    private fun loadRegisterReaderCard(userID: String) {

        viewLifecycleOwner.lifecycleScope.launch {
            val user = UserRepository().getUserById(userID)
            user?.let {
                fullNameET.setText(user.fullname)
                emailET.setText(user.email)
                val formattedBirthday = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                    .format(user.birthday)
                birthdayET.setText(formattedBirthday)            }
        }

        submitBtn.setOnClickListener {
            val selectedType = when (typeRG.checkedRadioButtonId) {
                R.id.radio_gold -> "GOLD"
                R.id.radio_silver -> "SILVER"
                R.id.radio_bronze -> "BRONZE"
                else -> null
            }

            val fullName = fullNameET.text.toString().trim()
            val email = emailET.text.toString().trim()
            val birthdayStr = birthdayET.text.toString().trim()
            val address = addressET.text.toString().trim()

            val birthdayDate = try {
                SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).parse(birthdayStr)
            } catch (e: Exception) {
                null
            }

            if (fullName.isEmpty()) {
                Toast.makeText(requireContext(), "Please enter your full name.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (email.isEmpty()) {
                Toast.makeText(requireContext(), "Please enter your email.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (birthdayDate == null || birthdayDate > Date()) {
                Toast.makeText(requireContext(), "Please enter a valid birth date (dd/MM/yyyy).", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (address.isEmpty()) {
                Toast.makeText(requireContext(), "Please enter your address.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (selectedType == null) {
                Toast.makeText(requireContext(), "Please select a card type.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val cardRequest = CardRequest(
                readerId = userID,
                fullName = fullName,
                birthday = birthdayDate,
                address = address,
                email = email,
                type = selectedType,
                requestAt = Date(),
                status = RequestStatus.PENDING.value
            )

            viewLifecycleOwner.lifecycleScope.launch {
                try {
                    cardRequestRepository.submitCardRequest(cardRequest)
                    Toast.makeText(requireContext(), "Request sent successfully", Toast.LENGTH_SHORT).show()
                    parentFragmentManager.popBackStack()
                } catch (e: Exception) {
                    Toast.makeText(requireContext(), "Request fail to sent: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }

        returnBtn.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }
}
