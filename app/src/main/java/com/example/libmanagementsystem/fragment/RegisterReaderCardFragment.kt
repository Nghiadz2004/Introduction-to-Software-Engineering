package com.example.libmanagementsystem.fragment

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
import com.example.libmanagementsystem.R
import androidx.lifecycle.lifecycleScope
import com.example.libmanagementsystem.model.CardRequest
import com.example.libmanagementsystem.model.RequestStatus
import com.example.libmanagementsystem.repository.CardRequestRepository
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

    private lateinit var userID: String

    private val cardRequestRepository = CardRequestRepository()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_register_reader_card, container, false)
    }

    companion object {
        fun newInstance(userID: String): RegisterReaderCardFragment {
            val fragment = RegisterReaderCardFragment()
            val args = Bundle()
            args.putString("USER_ID", userID)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        userID = arguments?.getString("USER_ID") ?: ""

        fullNameET = view.findViewById(R.id.fullnameET)
        emailET = view.findViewById(R.id.emailET)
        birthdayET = view.findViewById(R.id.birthdayET)
        addressET = view.findViewById(R.id.addressET)
        typeRG = view.findViewById(R.id.typeRG)
        submitBtn = view.findViewById(R.id.submitBtn)
        returnBtn = view.findViewById(R.id.returnBtn)

        submitBtn.setOnClickListener {
            val selectedType = when (typeRG.checkedRadioButtonId) {
                R.id.radio_gold -> "Vàng"
                R.id.radio_silver -> "Bạc"
                R.id.radio_bronze -> "Đồng"
                else -> null
            }

            if (selectedType == null) {
                Toast.makeText(requireContext(), "Vui lòng chọn loại thẻ", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val fullName = fullNameET.text.toString().trim()
            val email = emailET.text.toString().trim()
            val birthdayStr = birthdayET.text.toString().trim()
            val address = addressET.text.toString().trim()

            if (fullName.isEmpty() || email.isEmpty() || birthdayStr.isEmpty() || address.isEmpty()) {
                Toast.makeText(requireContext(), "Vui lòng điền đầy đủ thông tin", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val birthdayDate = try {
                SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).parse(birthdayStr)
            } catch (e: Exception) {
                null
            }

            if (birthdayDate == null) {
                Toast.makeText(requireContext(), "Ngày sinh không hợp lệ (dd/MM/yyyy)", Toast.LENGTH_SHORT).show()
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
                    Toast.makeText(requireContext(), "Gửi yêu cầu thành công", Toast.LENGTH_SHORT).show()
                    parentFragmentManager.popBackStack()
                } catch (e: Exception) {
                    Toast.makeText(requireContext(), "Gửi yêu cầu thất bại: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }

        returnBtn.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }
}
