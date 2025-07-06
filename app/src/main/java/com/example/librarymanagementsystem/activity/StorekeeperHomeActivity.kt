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
import com.example.librarymanagementsystem.fragment.AddBookFragment
import com.example.librarymanagementsystem.fragment.HomeSearchFragment
import com.example.librarymanagementsystem.fragment.StorekeeperHomeFragment
import com.example.librarymanagementsystem.model.Book
import com.example.librarymanagementsystem.repository.BookRepository
import kotlinx.coroutines.launch

private const val HOME_ID = "HOME"
private const val TRANSACTION_ID = "TRANSACTION"
private const val STATISTIC_ID = "STATISTIC"
private const val PROFILE_ID = "PROFILE"

private const val ALLBOOK_ID = "ALLBOOK"
private const val ADDBOOK_ID = "ADDBOOK"

class StorekeeperHomeActivity: AppCompatActivity() {
    //Initialize necessary variable
    private lateinit var bookRepository: BookRepository
    private lateinit var errorDialog: ErrorDialog
    private lateinit var userID: String

    private lateinit var searchView: SearchView

    private lateinit var homeBtn: Button
    private lateinit var transactionBtn: Button
    private lateinit var statisticBtn: Button
    private lateinit var profileBtn: Button

    private var currentFragment: Fragment? = null
    private lateinit var btnAllBook: Button
    private lateinit var btnAddBook: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_storekeeper_home)

        val currentUser = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser
        if (currentUser == null) {
            Log.e("StorekeeperHomeActivity", "User not logged in.")
            //Navigate to login
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish() // Đóng activity hiện tại nếu không cần giữ lại
        }
        userID = currentUser!!.uid

        // Initialize book repository
        bookRepository = BookRepository()

        // Initialize error dialog
        errorDialog = ErrorDialog(this, "Error")

        homeBtn = findViewById(R.id.stoHomeBtn)
        transactionBtn = findViewById(R.id.stoTransBtn)
        statisticBtn = findViewById(R.id.stoStatBtn)
        profileBtn = findViewById(R.id.stoProfileBtn)

        // Initialize necessary variables
        btnAllBook = findViewById(R.id.mnbAllBookBtn)
        btnAddBook = findViewById(R.id.mnbAddBookBtn)
        searchView = findViewById(R.id.searchView)

        //Handle all book button
        btnAllBook.setOnClickListener {
            switchFragment(ALLBOOK_ID)
        }

        //Handle AddBook button
        btnAddBook.setOnClickListener {
            switchFragment(ADDBOOK_ID)
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
        Log.d("StorekeeperHomeActivity", "Switching to fragment: $fragmentId")
        if (currentFragment is StorekeeperHomeFragment &&
            (currentFragment as? StorekeeperHomeFragment)?.getFragmentId() == fragmentId) {
            return
        }
        Log.d("StorekeeperHomeActivity", "Switching to fragment success")

        val fragment: Fragment = when (fragmentId) {
            ALLBOOK_ID -> StorekeeperHomeFragment().apply {
                arguments = Bundle().apply {
                    putString("FRAGMENT_ID", ALLBOOK_ID)
                }
            }
            ADDBOOK_ID -> AddBookFragment()
            else -> StorekeeperHomeFragment().apply {
                arguments = Bundle().apply {
                    putString("FRAGMENT_ID", ALLBOOK_ID)
                }
            }
        }

        supportFragmentManager.beginTransaction()
            .replace(R.id.fgManageBook, fragment)
            .addToBackStack(null)
            .commit()

        currentFragment = fragment
    }

    private fun handleMenuButton() {
        homeBtn.setOnClickListener {
            val intent = Intent(this, StorekeeperHomeActivity::class.java)
            intent.putExtra("PAGE_ID", HOME_ID)
            startActivity(intent)
            finish()
        }

        transactionBtn.setOnClickListener {
            val intent = Intent(this, StorekeeperTransactionActivity::class.java)
            intent.putExtra("PAGE_ID", TRANSACTION_ID)
            startActivity(intent)
            finish()
        }

        statisticBtn.setOnClickListener {
            val intent = Intent(this, ActivityStorekeeper::class.java)
            intent.putExtra("PAGE_ID", STATISTIC_ID)
            startActivity(intent)
            finish()
        }

        profileBtn.setOnClickListener {
            val intent = Intent(this, ActivityStorekeeper::class.java)
            intent.putExtra("PAGE_ID", PROFILE_ID)
            startActivity(intent)
            finish()
        }
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
                    Toast.makeText(this@StorekeeperHomeActivity, "No matching books found", Toast.LENGTH_SHORT).show()
                }

                val fragment = HomeSearchFragment.newInstance(searchResults)
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fgManageBook, fragment)
                    .addToBackStack(null)
                    .commit()

            } catch (e: Exception) {
                loadingDialog.dismiss()
                e.printStackTrace()
                Log.d("SEARCH", "Error: ${e.message}")
                Toast.makeText(this@StorekeeperHomeActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}