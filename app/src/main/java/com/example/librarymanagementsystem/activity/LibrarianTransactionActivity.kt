package com.example.librarymanagementsystem.activity

import android.content.res.ColorStateList
import android.os.Bundle
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
//import com.example.librarymanagementsystem.adapter.ReportLostAdapter
import com.example.librarymanagementsystem.service.LoanService
import com.example.librarymanagementsystem.service.QueueService
import com.example.librarymanagementsystem.service.ReturnBookService
//import com.example.librarymanagementsystem.service.ReportLostService
import kotlinx.coroutines.launch

class LibrarianTransactionActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manage_transaction)

        val btnLoans = findViewById<Button>(R.id.btnLoans)
        val btnQueues = findViewById<Button>(R.id.btnQueues)
        val btnReturnBook = findViewById<Button>(R.id.btnReturnBook)
        val btnReportLost = findViewById<Button>(R.id.btnReportLost)

        val recyclerView = findViewById<RecyclerView>(R.id.rvReaderCards)
        recyclerView.layoutManager = LinearLayoutManager(this)

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
                recyclerView.adapter = LoanAdapter(loanDisplays)
            }
        }

        btnQueues.setOnClickListener {
            setActiveTab(btnQueues, listOf(btnLoans, btnReturnBook, btnReportLost))
            lifecycleScope.launch {
                val queueDisplays = QueueService().getAllQueueDisplays()
                recyclerView.adapter = QueueAdapter(queueDisplays)
            }
        }

        btnReturnBook.setOnClickListener {
            setActiveTab(btnReturnBook, listOf(btnLoans, btnQueues, btnReportLost))
            lifecycleScope.launch {
                val returnDisplays = ReturnBookService().getAllReturnDisplays()
                recyclerView.adapter = ReturnBookAdapter(returnDisplays)
            }
        }

        btnReportLost.setOnClickListener {
            setActiveTab(btnReportLost, listOf(btnLoans, btnQueues, btnReturnBook))
            lifecycleScope.launch {
                //val lostDisplays = ReportLostService().getAllLostDisplays()
                //recyclerView.adapter = ReportLostAdapter(lostDisplays)
            }
        }
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
}