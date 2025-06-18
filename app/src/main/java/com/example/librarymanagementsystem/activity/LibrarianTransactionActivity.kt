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
import com.google.firebase.auth.FirebaseAuth

private const val HOME_ID = "HOME"
private const val TRANSACTION_ID = "TRANSACTION"
private const val STATISTIC_ID = "STATISTIC"
private const val PROFILE_ID = "PROFILE"

private const val LOAN_ID = "LOAN"
private const val QUEUE_ID = "QUEUE"
private const val RETURN_ID = "RETURN"
private const val REPORT_ID = "REPORT"

class LibrarianTransactionActivity: AppCompatActivity() {
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
    private lateinit var btnLoans: Button
    private lateinit var btnQueues: Button
    private lateinit var btnReturnBook: Button
    private lateinit var btnReportLost: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_manage_transaction)

        val pageID = intent.getStringExtra("PAGE_ID") ?: HOME_ID
        Log.d("LibrarianTransactionActivity", "pageID = $pageID")

        val userID = "6nohm4isYGYW7o7XMyEdDQ2a2um2"

        // Initialize book repository
        bookRepository = BookRepository()

        // Initialize error dialog
        errorDialog = ErrorDialog(this, "Error")

        homeBtn = findViewById(R.id.libHomeBtn)
        transactionBtn = findViewById(R.id.libTransBtn)
        statisticBtn = findViewById(R.id.libStatBtn)
        profileBtn = findViewById(R.id.libProfileBtn)

        // Initialize necessary variables
        btnLoans = findViewById(R.id.btnLoans)
        btnQueues = findViewById(R.id.btnQueues)
        btnReturnBook = findViewById(R.id.btnReturnBook)
        btnReportLost = findViewById(R.id.btnReportLost)

        //Handle loan button
        btnLoans.setOnClickListener {
            switchFragment(LOAN_ID)
        }

        //Handle queue button
        btnQueues.setOnClickListener {
            switchFragment(QUEUE_ID)
        }

        //Handle return book button
        btnReturnBook.setOnClickListener {
            switchFragment(RETURN_ID)
        }

        //Handle report lost button
        btnReportLost.setOnClickListener {
            switchFragment(REPORT_ID)
        }

        // Mặc định hiển thị loan
        if (savedInstanceState == null) {
            switchFragment(LOAN_ID)
        }

        handleMenuButton(userID)
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

    private fun handleMenuButton(userID: String) {
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