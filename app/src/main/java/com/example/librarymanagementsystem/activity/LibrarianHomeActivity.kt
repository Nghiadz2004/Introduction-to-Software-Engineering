package com.example.librarymanagementsystem.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.librarymanagementsystem.R
import com.example.librarymanagementsystem.dialog.ErrorDialog
import com.example.librarymanagementsystem.dialog.LoadingDialog
import com.example.librarymanagementsystem.fragment.HomeSearchFragment
import com.example.librarymanagementsystem.fragment.LibrarianHomeFragment
import com.example.librarymanagementsystem.model.Book
import com.example.librarymanagementsystem.repository.BookRepository
import kotlinx.coroutines.launch

private const val HOME_ID = "HOME"
private const val TRANSACTION_ID = "TRANSACTION"
private const val STATISTIC_ID = "STATISTIC"
private const val PROFILE_ID = "PROFILE"

private const val ALLBOOK_ID = "ALLBOOK"
private const val RDCARD_ID = "RDCARD"
private const val ADD_RDCARD_ID = "ADD_RDCARD"

class LibrarianHomeActivity: AppCompatActivity() {
    //Initialize necessary variable
    private lateinit var bookRepository: BookRepository
    private lateinit var errorDialog: ErrorDialog
    private lateinit var userID: String

    private lateinit var homeBtn: Button
    private lateinit var transactionBtn: Button
    private lateinit var statisticBtn: Button
    private lateinit var profileBtn: Button

    private var currentFragment: Fragment? = null
    private lateinit var btnAllBook: Button
    private lateinit var btnRdcard: Button
    private lateinit var btnAddRdcard: Button
    private lateinit var searchView: SearchView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_manage_book)

        userID = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser?.uid ?: ""
        if (userID == null) {
            Log.e("LibrarianHomeActivity", "User not logged in.")
            //Navigate to login
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish() // Đóng activity hiện tại nếu không cần giữ lại
        }


        // Initialize book repository
        bookRepository = BookRepository()

        // Initialize error dialog
        errorDialog = ErrorDialog(this, "Error")

        homeBtn = findViewById(R.id.libHomeBtn)
        transactionBtn = findViewById(R.id.libTransBtn)
        statisticBtn = findViewById(R.id.libStatBtn)
        profileBtn = findViewById(R.id.libProfileBtn)

        // Initialize necessary variables
        btnAllBook = findViewById(R.id.mnbAllBookBtn)
        btnRdcard = findViewById(R.id.mnbRdcardBtn)
        btnAddRdcard = findViewById(R.id.mnbAddRdcardBtn)
        searchView = findViewById(R.id.searchView)

        //Handle all book button
        btnAllBook.setOnClickListener {
            switchFragment(ALLBOOK_ID)
        }

        //Handle ReaderCard button
        btnRdcard.setOnClickListener {
            switchFragment(RDCARD_ID)
        }

        //Handle Add ReaderCard button
        btnAddRdcard.setOnClickListener {
            switchFragment(ADD_RDCARD_ID)
        }

        // Mặc định hiển thị AllBook
        if (savedInstanceState == null) {
            switchFragment(ALLBOOK_ID)
        }

        searchView.setOnClickListener {
            // Làm gì đó khi toàn bộ SearchView được click
            searchView.setIconified(false) // Để thanh tìm kiếm mở rộng ngay khi click
        }

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let {
                    performSearch(it)
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                // Gọi nếu muốn lọc realtime
                // performSearch(newText.orEmpty())
                return false
            }
        })

        handleMenuButton()
    }

    private fun switchFragment(fragmentId: String) {
        // Kiểm tra nếu currentFragment là AllBookFragment và có cùng ID thì không làm gì
        if (currentFragment is LibrarianHomeFragment &&
            (currentFragment as LibrarianHomeFragment).getFragmentId() == fragmentId) {
            return
        }

        val fragment = LibrarianHomeFragment().apply {
            arguments = Bundle().apply {
                putString("FRAGMENT_ID", fragmentId)
            }
        }

        supportFragmentManager.beginTransaction()
            .replace(R.id.fgManageBook, fragment)
            .addToBackStack(null)
            .commit()

        currentFragment = fragment
    }

    private fun performSearch(keyword: String) {
        // Ẩn bàn phím
        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(searchView.windowToken, 0)
        val loadingDialog = LoadingDialog(this)
        loadingDialog.show()
        lifecycleScope.launch {
            try {
                val searchResults: List<Book> = bookRepository.searchBooksByKeyword(keyword)
                loadingDialog.dismiss()
                if (searchResults.isEmpty()) {
                    Toast.makeText(this@LibrarianHomeActivity, "No matching books found", Toast.LENGTH_SHORT).show()
                }
//                val fragment = SearchHome.newInstance(searchResults)
//                supportFragmentManager.beginTransaction()
//                    .replace(R.id.fragmentContainer, fragment)
//                    .addToBackStack(null)
//                    .commit()
                val fragment = HomeSearchFragment.newInstance(searchResults)
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fgManageBook, fragment)
                    .addToBackStack(null)
                    .commit()

            } catch (e: Exception) {
                loadingDialog.dismiss()
                e.printStackTrace()
                Log.d("SEARCH", "Error: ${e.message}")
                Toast.makeText(this@LibrarianHomeActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun handleMenuButton() {
        homeBtn.setOnClickListener {
            val intent = Intent(this, LibrarianHomeActivity::class.java)
            intent.putExtra("PAGE_ID", HOME_ID)
            startActivity(intent)
            finish()
        }

        transactionBtn.setOnClickListener {
            val intent = Intent(this, LibrarianTransactionActivity::class.java)
            intent.putExtra("PAGE_ID", TRANSACTION_ID)
            startActivity(intent)
            finish()
        }

        statisticBtn.setOnClickListener {
            val intent = Intent(this, ActivityLibrarian::class.java)
            intent.putExtra("PAGE_ID", STATISTIC_ID)
            startActivity(intent)
            finish()
        }

        profileBtn.setOnClickListener {
            val intent = Intent(this, ActivityLibrarian::class.java)
            intent.putExtra("PAGE_ID", PROFILE_ID)
            startActivity(intent)
            finish()
        }
    }
}