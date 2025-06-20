package com.example.librarymanagementsystem.activity

import android.annotation.SuppressLint
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
import com.example.librarymanagementsystem.cache.FavoriteCache
import com.example.librarymanagementsystem.cache.LibraryCardCache
import com.example.librarymanagementsystem.databinding.ActivityDetailBookBinding
import com.google.firebase.auth.FirebaseAuthException
import com.example.librarymanagementsystem.model.Book
import com.example.librarymanagementsystem.repository.BookRepository
import kotlinx.coroutines.launch
import com.example.librarymanagementsystem.dialog.ErrorDialog
import com.example.librarymanagementsystem.model.BorrowRequest
import com.example.librarymanagementsystem.repository.BorrowingRepository
import com.example.librarymanagementsystem.repository.FavoriteRepository
import com.example.librarymanagementsystem.repository.RequestBorrowRepository
import com.example.librarymanagementsystem.service.UIService
import com.google.firebase.auth.FirebaseAuth

class ActivityDetailBook : AppCompatActivity() {
    //Initialize necessary variable
    private lateinit var auth: FirebaseAuth
    private lateinit var bookID: String
    private lateinit var binding: ActivityDetailBookBinding
    private lateinit var errorDialog: ErrorDialog
    private lateinit var btnBack: AppCompatImageButton
    private lateinit var btnFavorite: AppCompatImageButton
    private lateinit var btnBorrow: Button
    private lateinit var borrowingRepository: BorrowingRepository
    private lateinit var requestBorrowRepository: RequestBorrowRepository
    private var isFavorite: Boolean = false

    // function to display book details
    @SuppressLint("SetTextI18n")
    private fun displayBookDetails(book: Book, binding: ActivityDetailBookBinding) {
        Glide.with(this).load(book.cover).into(binding.bdBookCover)
        borrowingRepository = BorrowingRepository()
        requestBorrowRepository = RequestBorrowRepository()
        var queue = 0
        var borrower = 0
        lifecycleScope.launch {
            queue = requestBorrowRepository.getNumBookPendingRequests(book.id.toString())
            borrower = borrowingRepository.getNumBorrowById(book.id.toString())
        }
        binding.bdQueue.text = "$queue Queues"
        binding.bdBorrower.text = "$borrower Borrower"
        binding.bdBookTitle.text = book.title
        binding.bdBookAuthor.text = book.author ?: "Unknown"
        binding.bdBookCategory.text = book.category
        binding.bdBookCopyNumber.text = book.quantity.toString()
        binding.bdBookPublishDate.text = book.publishYear?.toString() ?: "Unknown"
        binding.bdBookPublisher.text = book.publisher ?: "Unknown"
        binding.bdSummary.text = book.summary ?: "No summary available"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        auth = FirebaseAuth.getInstance()
        val userId = auth.currentUser!!.uid
        setContentView(R.layout.activity_detail_book)
        val book: Book? = intent.getParcelableExtra("book")

        // Initialize binding
        binding = ActivityDetailBookBinding.inflate(layoutInflater)
        setContentView(binding.root)


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
        isFavorite = FavoriteCache.favoriteBookIds.contains(book!!.id!!)
        if (isFavorite) {
            UIService.setButtonIcon(
                this@ActivityDetailBook,
                btnFavorite,
                R.drawable.baseline_favorite_24
            )
            UIService.setButtonColor(
                this@ActivityDetailBook,
                btnFavorite,
                selectedColorResId = R.color.red
            )
        }
        btnBorrow = findViewById(R.id.bdBtnBorrow)

        //Handle add favorite button
        btnFavorite.setOnClickListener {
            //Handle add favorite button
            lifecycleScope.launch {
                if (isFavorite) {
                    UIService.setButtonIcon(
                        this@ActivityDetailBook,
                        btnFavorite,
                        R.drawable.favorite_icon
                    )
                    UIService.setButtonColor(
                        this@ActivityDetailBook,
                        btnFavorite,
                        selectedColorResId = R.color.white
                    )
                    isFavorite = false
                    // Remove from cache
                    FavoriteCache.favoriteBookIds.remove(book.id)
                }
                else {
                    UIService.setButtonIcon(
                        this@ActivityDetailBook,
                        btnFavorite,
                        R.drawable.baseline_favorite_24
                    )
                    UIService.setButtonColor(
                        this@ActivityDetailBook,
                        btnFavorite,
                        selectedColorResId = R.color.red
                    )
                    isFavorite = true
                    // Add to cache
                    FavoriteCache.favoriteBookIds.add(book.id!!)
                }
                // Update database
                FavoriteRepository().updateFavorite(userId, FavoriteCache.favoriteBookIds)
            }
        }



        //Handle borrow button
        btnBorrow.setOnClickListener {
            //Handle borrow button
            if (LibraryCardCache.libraryCard != null) {
                val request = BorrowRequest(libraryCardId = LibraryCardCache.libraryCard!!.id!!, readerId = userId, bookId = bookID)
                lifecycleScope.launch {
                    requestBorrowRepository.addRequestBorrow(request)
                }
            }
        }

        //Handle back button to return previous page
        btnBack.setOnClickListener {
            finish()
        }

        lifecycleScope.launch {
            try {
                displayBookDetails(book, binding)
                bookID = book.id.toString()
            } catch (e: Exception) {
                // Handle exception
                errorDialog = ErrorDialog(this@ActivityDetailBook, "Có lỗi xảy ra T.T Vui lòng thử lại sau ~~", onDismissCallback = {
                    finish()
                })
                errorDialog.show()
            }
        }
    }
}