package com.example.librarymanagementsystem.activity

import android.os.Bundle
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
import com.google.firebase.auth.FirebaseAuthException
import com.example.librarymanagementsystem.model.Book
import com.example.librarymanagementsystem.repository.BookRepository
import kotlinx.coroutines.launch
import com.example.librarymanagementsystem.dialog.ErrorDialog

class ActivityDetailBook : AppCompatActivity() {
    //Initialize necessary variable
    private lateinit var auth: FirebaseAuthException
    private lateinit var bookID: String
    private lateinit var binding: ActivityDetailBookBinding
    private lateinit var bookRepository: BookRepository
    private lateinit var errorDialog: ErrorDialog
    private lateinit var btnBack: AppCompatImageButton
    private lateinit var btnFavorite: AppCompatImageButton
    private lateinit var btnBorrow: Button

    // function to display book details
    private fun displayBookDetails(book: Book, binding: ActivityDetailBookBinding) {
        Glide.with(this).load(book.cover).into(binding.bdBookCover)
        val queue = 0
        val borrower = 0
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

        // Nhận sách
//        val book = intent.getParcelableExtra<Book>("book")

        // Initialize binding
        binding = ActivityDetailBookBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize book repository
        bookRepository = BookRepository()

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

//        bookID = intent.getStringExtra("BOOK_ID") ?: ""

        lifecycleScope.launch {
            try {
                val book: Book? = intent.getParcelableExtra<Book>("book")
//                val book = bookRepository.getBookById(bookID)
                if (book != null) {
                    displayBookDetails(book, binding)
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