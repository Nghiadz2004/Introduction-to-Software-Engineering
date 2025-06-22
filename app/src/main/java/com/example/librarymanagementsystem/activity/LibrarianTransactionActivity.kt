package com.example.librarymanagementsystem.activity

import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.librarymanagementsystem.R
import com.example.librarymanagementsystem.adapter.LoanAdapter
import com.example.librarymanagementsystem.adapter.QueueAdapter
import com.example.librarymanagementsystem.adapter.ReturnBookAdapter
import com.example.librarymanagementsystem.adapter.ReportLostAdapter
import com.example.librarymanagementsystem.service.LoanService
import com.example.librarymanagementsystem.service.QueueService
import com.example.librarymanagementsystem.service.ReturnBookService
import com.example.librarymanagementsystem.service.ReportLostService
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
//import com.example.librarymanagementsystem.service.ReportLostService
import kotlinx.coroutines.launch

private const val HOME_ID = "HOME"
private const val TRANSACTION_ID = "TRANSACTION"
private const val STATISTIC_ID = "STATISTIC"
private const val PROFILE_ID = "PROFILE"

class LibrarianTransactionActivity : AppCompatActivity() {
    private lateinit var homeBtn: Button
    private lateinit var transactionBtn: Button
    private lateinit var statisticBtn: Button
    private lateinit var profileBtn: Button
    private lateinit var auth: FirebaseAuth
    private lateinit var userID: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_manage_transaction)

        val pageID = intent.getStringExtra("PAGE_ID") ?: HOME_ID
        auth = Firebase.auth
        userID = auth.currentUser!!.uid
        homeBtn = findViewById(R.id.libHomeBtn)
        transactionBtn = findViewById(R.id.libTransBtn)
        statisticBtn = findViewById(R.id.libStatBtn)
        profileBtn = findViewById(R.id.libProfileBtn)
        val btnLoans = findViewById<Button>(R.id.btnLoans)
        val btnQueues = findViewById<Button>(R.id.btnQueues)
        val btnReturnBook = findViewById<Button>(R.id.btnReturnBook)
        val btnReportLost = findViewById<Button>(R.id.btnReportLost)

        val recyclerView = findViewById<RecyclerView>(R.id.fgTransaction)
        recyclerView.layoutManager = LinearLayoutManager(this)

        handleMenuButton(userID)

        // Mặc định hiển thị Loans
        setActiveTab(btnLoans, listOf(btnQueues, btnReturnBook, btnReportLost))
        lifecycleScope.launch {
            val loanDisplays = LoanService().getAllLoanDisplays()
            recyclerView.adapter = LoanAdapter(loanDisplays)
        }

        btnLoans.setOnClickListener {
            setActiveTab(btnLoans, listOf(btnQueues, btnReturnBook, btnReportLost))
            lifecycleScope.launch {
                val loanDisplays = LoanService().getAllLoanDisplays()
                Log.d("LOAN_SERVICE", "${loanDisplays.size}")
                recyclerView.adapter = LoanAdapter(loanDisplays)
            }
        }

        btnQueues.setOnClickListener {
            setActiveTab(btnQueues, listOf(btnLoans, btnReturnBook, btnReportLost))
            lifecycleScope.launch {
                reloadQueueBooks(recyclerView, userID)
            }
        }

        btnReturnBook.setOnClickListener {
            setActiveTab(btnReturnBook, listOf(btnLoans, btnQueues, btnReportLost))
            lifecycleScope.launch {
                reloadReturnBooks(recyclerView)
            }
        }

        btnReportLost.setOnClickListener {
            setActiveTab(btnReportLost, listOf(btnLoans, btnQueues, btnReturnBook))
            lifecycleScope.launch {
                reloadLostReports(recyclerView, userID)
            }
        }
    }

    private suspend fun reloadQueueBooks(recyclerView: RecyclerView, userId: String) {
        val updated = QueueService().getAllQueueDisplays()

        recyclerView.adapter = QueueAdapter(updated, userId, onQueueChanged = {
            lifecycleScope.launch {
                reloadQueueBooks(recyclerView, userId)
            }
        })
    }

    private suspend fun reloadLostReports(recyclerView: RecyclerView, librarianId: String) {
        val updated = ReportLostService().getAllLostDisplays()
        recyclerView.adapter = ReportLostAdapter(
            lostList = updated,
            librarianId = librarianId,
            onLostChanged = {
                reloadLostReports(recyclerView, librarianId)
            }
        )
    }

    private suspend fun reloadReturnBooks(recyclerView: RecyclerView) {
        val updated = ReturnBookService().getAllReturnDisplays()
        recyclerView.adapter = ReturnBookAdapter(updated, onReturnChanged = {
            reloadReturnBooks(recyclerView)
        })
    }

    private fun setActiveTab(active: Button, others: List<Button>) {
        val pink = ContextCompat.getColor(this, R.color.pink)
        val lightPurple = ContextCompat.getColor(this, R.color.light_purple)

        active.setTextColor(pink)
        active.compoundDrawableTintList = ColorStateList.valueOf(pink)

        for (btn in others) {
            btn.setTextColor(lightPurple)
            btn.compoundDrawableTintList = ColorStateList.valueOf(lightPurple)
        }
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