package com.example.librarymanagementsystem.activity

import android.content.Context
import android.graphics.Typeface
import android.os.Bundle
import android.text.InputType
import android.util.TypedValue
import android.view.Gravity
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageButton
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.librarymanagementsystem.R
import com.example.librarymanagementsystem.cache.BookOperateCache
import com.example.librarymanagementsystem.cache.FavoriteCache
import com.example.librarymanagementsystem.cache.LibraryCardCache
import com.example.librarymanagementsystem.databinding.ActivityDetailBookBinding
import com.example.librarymanagementsystem.model.Book
import kotlinx.coroutines.launch
import com.example.librarymanagementsystem.dialog.ErrorDialog
import com.example.librarymanagementsystem.repository.BorrowingRepository
import com.example.librarymanagementsystem.repository.FavoriteRepository
import com.example.librarymanagementsystem.repository.LibraryCardRepository
import com.example.librarymanagementsystem.repository.RequestBorrowRepository
import com.example.librarymanagementsystem.service.BorrowBookManager
import com.example.librarymanagementsystem.service.UIService
import com.google.firebase.auth.FirebaseAuth

class ActivityDetailBook : AppCompatActivity() {
    //Initialize necessary variable
    private lateinit var auth: FirebaseAuth
    private lateinit var bookID: String
    private lateinit var category: String
    private lateinit var binding: ActivityDetailBookBinding
    private lateinit var borrowRepository: BorrowingRepository
    private lateinit var requestBorrowRepository: RequestBorrowRepository
    private lateinit var borrowManager: BorrowBookManager
    private lateinit var errorDialog: ErrorDialog
    private lateinit var btnBack: AppCompatImageButton
    private lateinit var btnFavorite: AppCompatImageButton
    private lateinit var btnBorrow: Button
    private var isFavorite: Boolean = false

    private fun showInputDialog(
        context: Context,
        title: String,
        onInputConfirmed: (Int) -> Unit // 👉 trả về Int thay vì String
    ) {
        val titleView = TextView(context).apply {
            text = title
            gravity = Gravity.CENTER
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 20f)
            setTypeface(null, Typeface.BOLD)
            setPadding(30, 40, 30, 20)
        }

        val editText = EditText(context).apply {
            inputType = InputType.TYPE_CLASS_NUMBER // 👉 chỉ cho phép nhập số
            hint = "Days to borrow"
            gravity = Gravity.CENTER
        }

        val dialog = AlertDialog.Builder(context)
            .setCustomTitle(titleView)
            .setView(editText)
            .setPositiveButton("OK", null) // 👉 set null để tự xử lý click
            .setNegativeButton("Cancel", null)
            .create()

        dialog.setOnShowListener {
            val okButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
            okButton.setOnClickListener {
                val input = editText.text.toString().trim()
                val days = input.toIntOrNull()

                when {
                    input.isEmpty() -> {
                        editText.error = "Please fill in the number"
                    }
                    days == null -> {
                        editText.error = "Input must be a valid number"
                    }
                    days <= 0 -> {
                        editText.error = "Borrow period must be greater than 0"
                    }
                    days > 30 -> {
                        editText.error = "Maximum borrow period is 30 days"
                    }
                    else -> {
                        dialog.dismiss()
                        onInputConfirmed(days)
                    }
                }

            }
        }

        dialog.show()
    }



    // function to display book details
    private fun displayBookDetails(book: Book, binding: ActivityDetailBookBinding, queue: Int, borrower: Int) {
        Glide.with(this).load(book.cover).into(binding.bdBookCover)
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
        bookID = book!!.id.toString()
        category = book.category
        // Initialize binding
        binding = ActivityDetailBookBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize necessary repositories
        borrowRepository = BorrowingRepository()
        requestBorrowRepository = RequestBorrowRepository()

        // Initialize necessary managers
        borrowManager = BorrowBookManager(requestBorrowRepository, borrowRepository)

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
        isFavorite = FavoriteCache.favoriteBookIds.contains(book.id!!)
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
                    Toast.makeText(this@ActivityDetailBook, "Removed from favorites", Toast.LENGTH_SHORT).show()
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
                    FavoriteCache.favoriteBookIds.add(book.id)
                    Toast.makeText(this@ActivityDetailBook, "Add to favorites", Toast.LENGTH_SHORT).show()

                }
                // Update database
                FavoriteRepository().updateFavorite(userId, FavoriteCache.favoriteBookIds)
            }
        }

        if (BookOperateCache.statusMap.containsKey(bookID)) {
            if (BookOperateCache.statusMap[bookID] == "PENDING_LOST") {
                btnBorrow.text = "BORROWED"
            }
            else {
                btnBorrow.text = BookOperateCache.statusMap[bookID]
            }
        }

        btnBorrow.setOnClickListener {
            // Lấy thông tin thẻ thư viện
            val card = LibraryCardCache.libraryCard
            if (card == null) {
                Toast.makeText(this, "Please register for a library card before borrowing books", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Xử lý trường hợp sách đã mượn
            val isBorrowed = BookOperateCache.statusMap[bookID] == "BORROWED" || BookOperateCache.statusMap[bookID] == "PENDING_LOST"
            if (isBorrowed) {
                return@setOnClickListener
            }

            // Xử lý trường hợp sách đang chờ mượn
            val isPending = BookOperateCache.statusMap[bookID] == "PENDING"
            if (isPending) {
                // ---- Huỷ yêu cầu đang chờ ----
                lifecycleScope.launch {
                    RequestBorrowRepository()
                        .cancelPendingRequest(
                            bookID,
                            card.readerId)

                    BookOperateCache.statusMap.remove(bookID)
                    btnBorrow.text = "BORROW"
                }
                Toast.makeText(this, "Book borrowing request cancelled successfully", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // ---- Kiểm tra điều kiện mượn trước khi gửi yêu cầu mới ----
            lifecycleScope.launch {
                val eligibility = borrowManager.checkBorrowEligibility(
                    card!!.readerId
                )

                if (!eligibility.ok) {
                    // Không hợp lệ ⇒ thông báo lỗi & thoát
                    Toast.makeText(this@ActivityDetailBook, eligibility.message, Toast.LENGTH_LONG).show()
                    return@launch
                }

                // ---- Đủ ĐK ⇒ hỏi số ngày mượn rồi gửi yêu cầu ----
                showInputDialog(this@ActivityDetailBook, "Input borrow period") { inputDays ->
                    lifecycleScope.launch {
                        requestBorrowRepository.addRequestBorrow(
                            libraryCardId = card.requestId,
                            readerId      = userId,
                            bookId        = bookID,
                            daysBorrow    = inputDays,
                            category      = category
                        )

                        btnBorrow.text = "PENDING"
                        BookOperateCache.statusMap[bookID] = "PENDING"
                        Toast.makeText(this@ActivityDetailBook, "Book borrowing request sent successfully", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        //Handle back button to return previous page
        btnBack.setOnClickListener {
            finish()
        }

        lifecycleScope.launch {
            try {
                val book: Book? = intent.getParcelableExtra<Book>("book")
                if (book != null) {
                    val queue = requestBorrowRepository.getNumBookPendingRequests(book.id!!)
                    val borrower = borrowRepository.getNumBorrowById(book.id)
                    displayBookDetails(book, binding, queue, borrower)
                }
                else {
                    errorDialog = ErrorDialog(this@ActivityDetailBook, "Book not found or no longer exists", onDismissCallback = {
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