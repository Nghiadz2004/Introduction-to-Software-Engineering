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
import com.example.librarymanagementsystem.repository.BookRepository
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth

private const val HOME_ID = "HOME"
private const val TRANSACTION_ID = "TRANSACTION"
private const val STATISTIC_ID = "STATISTIC"
private const val PROFILE_ID = "PROFILE"

private const val ALLBOOK_ID = "ALLBOOK"
private const val RDCARD_ID = "RDCARD"
private const val ADD_RDCARD_ID = "ADD_RDCARD"

class LibrarianHomeActivity: AppCompatActivity() {
    //Initialize necessary variable
    private lateinit var auth: FirebaseAuth
    private lateinit var bookID: String
    private lateinit var bookRepository: BookRepository
    private lateinit var errorDialog: ErrorDialog

    private lateinit var homeBtn: Button
    private lateinit var transactionBtn: Button
    private lateinit var statisticBtn: Button
    private lateinit var profileBtn: Button

    private var currentFragment: Fragment? = null
    private lateinit var btnAllBook: Button
    private lateinit var btnRdcard: Button
    private lateinit var btnAddRdcard: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_manage_book)
        val pageID = intent.getStringExtra("PAGE_ID") ?: HOME_ID

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