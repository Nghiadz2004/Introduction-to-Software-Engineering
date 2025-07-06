package com.example.librarymanagementsystem.fragment

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.librarymanagementsystem.R
import com.example.librarymanagementsystem.dialog.LoadingDialog
import com.example.librarymanagementsystem.model.Book
import com.example.librarymanagementsystem.repository.BookRepository
import com.example.librarymanagementsystem.service.ImageManager
import kotlinx.coroutines.launch

class AddBookFragment : Fragment() {
    private lateinit var avatarIV: ImageView
    private lateinit var avatarBtn: ImageButton
    private lateinit var imagePickerLauncher: ActivityResultLauncher<String>
    private lateinit var titleEditText: EditText
    private lateinit var authorEditText: EditText
    private lateinit var publisherEditText: EditText
    private lateinit var publishYearEditText: EditText
    private lateinit var quantityEditText: EditText
    private lateinit var priceEditText: EditText
    private lateinit var categoryEditText: EditText
    private lateinit var summaryEditText: EditText
    private lateinit var submitButton: Button

    private val bookRepository = BookRepository()

    private var selectedAvatarUri: Uri? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate layout cho Fragment
        return inflater.inflate(R.layout.fragment_add_book, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Tìm các view bằng view.findViewById thay vì findViewById trực tiếp
        avatarIV = view.findViewById(R.id.avatarIV)
        avatarBtn = view.findViewById(R.id.avatar_button)
        titleEditText = view.findViewById(R.id.titleEditText)
        authorEditText = view.findViewById(R.id.authorEditText)
        publisherEditText = view.findViewById(R.id.publisherEditText)
        publishYearEditText = view.findViewById(R.id.publishYearEditText)
        quantityEditText = view.findViewById(R.id.copyNumEditText)
        priceEditText = view.findViewById(R.id.priceEditText)
        categoryEditText = view.findViewById(R.id.categoryEditText)
        summaryEditText = view.findViewById(R.id.summaryEditText)
        submitButton = view.findViewById(R.id.submitButton)

        // Avatar picker
        imagePickerLauncher =
            registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
                uri?.let {
                    avatarIV.setImageURI(it)
                    selectedAvatarUri = it
                }
            }

        avatarBtn.setOnClickListener {
            imagePickerLauncher.launch("image/*")
        }

        submitButton.setOnClickListener {
            viewLifecycleOwner.lifecycleScope.launch {
                val title = titleEditText.text.toString().trim()
                val author = authorEditText.text.toString().trim()
                val publisher = publisherEditText.text.toString().trim()
                val publishYear = publishYearEditText.text.toString().trim().toIntOrNull()
                val quantity = quantityEditText.text.toString().trim().toIntOrNull()
                val price = priceEditText.text.toString().trim().toIntOrNull()
                val category = categoryEditText.text.toString().trim()
                val summary = summaryEditText.text.toString().trim()

                if (title.isEmpty() || publishYear == null || quantity == null || price == null) {
                    Toast.makeText(
                        requireContext(),
                        "Please fill all required fields!",
                        Toast.LENGTH_SHORT
                    ).show()
                    return@launch
                }

                if (price <= 0 || quantity <= 0 || publishYear <= 0) {
                    Toast.makeText(
                        requireContext(),
                        "Price, quantity, and publish year must be greater than 0!",
                        Toast.LENGTH_SHORT
                    ).show()
                    return@launch
                }

                val newBook = Book(
                    title = title,
                    author = author,
                    publisher = publisher,
                    publishYear = publishYear,
                    quantity = quantity,
                    price = price,
                    category = category,
                    summary = summary,
                )

                val loadingDialog = LoadingDialog(requireContext())
                loadingDialog.show()
                // Avatar upload
                if (selectedAvatarUri != null) {
                    try {
                        val imageManager = ImageManager(requireContext())

                        // Upload lên Cloudinary bằng ImageManager
                        val imageUrl = imageManager.uploadImageToCloudinary(selectedAvatarUri!!)

                        // Cập nhật trường avatar
                        newBook.cover = imageUrl

                        bookRepository.addBook(newBook)
                        loadingDialog.dismiss()
                        clearForm()
                        Toast.makeText(context, "Book added successfully!", Toast.LENGTH_SHORT).show()

                    } catch (e: Exception) {
                        Log.e("UploadAvatar", "Lỗi khi cập nhật avatar", e)
                        loadingDialog.dismiss()
                        Toast.makeText(requireContext(), "Lỗi: ${e.message}", Toast.LENGTH_LONG)
                            .show()
                    }
                } else {
                    // Không có avatar mới, chỉ cập nhật thông tin khác
                    try {
                        bookRepository.addBook(newBook)
                        clearForm()
                        loadingDialog.dismiss()
                        Toast.makeText(context, "Book added successfully!", Toast.LENGTH_SHORT).show()
                    } catch (e: Exception) {
                        Log.e("UploadAvatar", "Lỗi khi cập nhật avatar", e)
                        loadingDialog.dismiss()
                        Toast.makeText(requireContext(), "Lỗi: ${e.message}", Toast.LENGTH_LONG)
                            .show()
                    }
                }
            }
        }
    }

    private fun clearForm() {
        titleEditText.setText("")
        authorEditText.setText("")
        publisherEditText.setText("")
        publishYearEditText.setText("")
        quantityEditText.setText("")
        priceEditText.setText("")
        categoryEditText.setText("")
        summaryEditText.setText("")
    }
}


