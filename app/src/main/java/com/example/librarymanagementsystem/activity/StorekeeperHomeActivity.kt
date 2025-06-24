package com.example.librarymanagementsystem.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.librarymanagementsystem.R
import com.example.librarymanagementsystem.dialog.ErrorDialog
import com.example.librarymanagementsystem.fragment.LibrarianHomeFragment
import com.example.librarymanagementsystem.fragment.StorekeeperHomeFragment
import com.example.librarymanagementsystem.repository.BookRepository

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

        userID = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser?.uid ?: ""
        if (userID == null) {
            Log.e("StorekeeperHomeActivity", "User not logged in.")
            //Navigate to login
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish() // Đóng activity hiện tại nếu không cần giữ lại
        }

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

        handleMenuButton()
    }

    private fun switchFragment(fragmentId: String) {
        // Kiểm tra nếu currentFragment là AllBookFragment và có cùng ID thì không làm gì
        if (currentFragment is StorekeeperHomeFragment &&
            (currentFragment as StorekeeperHomeFragment).getFragmentId() == fragmentId) {
            return
        }
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
}