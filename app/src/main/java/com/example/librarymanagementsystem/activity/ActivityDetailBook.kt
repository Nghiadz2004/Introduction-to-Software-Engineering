package com.example.librarymanagementsystem.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageButton
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.librarymanagementsystem.R
import com.example.librarymanagementsystem.databinding.ActivityDetailBookBinding
import com.example.librarymanagementsystem.model.Book
import kotlinx.coroutines.launch
import com.example.librarymanagementsystem.dialog.ErrorDialog
import com.example.librarymanagementsystem.repository.BorrowingRepository
import com.example.librarymanagementsystem.repository.RequestBorrowRepository

class ActivityDetailBook : AppCompatActivity() {
    //Initialize necessary variable
    private lateinit var binding: ActivityDetailBookBinding
    private lateinit var borrowRepository: BorrowingRepository
    private lateinit var requestBorrowRepository: RequestBorrowRepository
    private lateinit var errorDialog: ErrorDialog
    private lateinit var btnBack: AppCompatImageButton
    private lateinit var btnFavorite: AppCompatImageButton
    private lateinit var btnBorrow: Button
    private lateinit var userID: String

    // function to display book details
    private fun displayBookDetails(book: Book, binding: ActivityDetailBookBinding, queue: Int, borrower: Int) {
        Glide.with(this).load(book.cover).into(binding.bdBookCover)
        binding.bdQueue.text = "$queue Queues"
        binding.bdBorrower.text = "$borrower Borrower"
        binding.bdBookTitle.text = book.title
        binding.bdBookAuthor.text = book.author ?: "Unknown"
        binding.bdBookCategory.text = book.category ?: "Unknown"
        binding.bdBookCopyNumber.text = book.quantity.toString()
        binding.bdBookPublishDate.text = book.publishYear?.toString() ?: "Unknown"
        binding.bdBookPublisher.text = book.publisher ?: "Unknown"
        binding.bdSummary.text = book.summary ?: "No summary available"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_detail_book)

        userID = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser?.uid ?: ""
        if (userID == null) {
            Log.e("ActivityDetailBook", "User not logged in.")
            //Navigate to login
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish() // Đóng activity hiện tại nếu không cần giữ lại
        }

        // Initialize binding
        binding = ActivityDetailBookBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize necessary repositories
        borrowRepository = BorrowingRepository()
        requestBorrowRepository = RequestBorrowRepository()

        // Initialize error dialog
        errorDialog = ErrorDialog(this, "Error")

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.activity_detail_book)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Initialize necessary variables
        btnBack = findViewById(R.id.bdBtnBack)
        btnFavorite = findViewById(R.id.bdBtnFavorite)
        btnBorrow = findViewById(R.id.bdBtnBorrow)

        //Handle add favorite button
        btnFavorite.setOnClickListener {
            //Handle add favorite button
        }

        //Handle borrow button
        btnBorrow.setOnClickListener {
            //Handle borrow button
        }

        //Handle back button to return previous page
        btnBack.setOnClickListener {
            finish()
        }

        lifecycleScope.launch {
            try {
                val book: Book? = intent.getParcelableExtra<Book>("book")
                if (book != null) {
                    val queues = requestBorrowRepository.getPendingRequests()
                    val queue = queues.count { it.bookId == book.id }
                    val borrower = borrowRepository.getNumBorrowById(book.id!!)
                    Log.d("ActivityDetailBook", "Queue: $queue")
                    Log.d("ActivityDetailBook", "Borrower: $borrower")
                    displayBookDetails(book, binding, queue, borrower)
                }
                else {
                    errorDialog = ErrorDialog(this@ActivityDetailBook, "Không tìm thấy sách hoặc sách không còn tồn tại", onDismissCallback = {
                        finish()
                    })
                    errorDialog.show()
                }
            } catch (e: Exception) {
                // Handle exception
                errorDialog = ErrorDialog(this@ActivityDetailBook, "Có lỗi xảy ra T.T\\nVui lòng thử lại sau ~~", onDismissCallback = {
                    finish()
                })
                errorDialog.show()
            }
        }
    }
}