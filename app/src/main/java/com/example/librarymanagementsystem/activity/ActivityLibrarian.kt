package com.example.librarymanagementsystem.activity

import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.librarymanagementsystem.R
import com.example.librarymanagementsystem.fragment.StatisticFragment
import com.example.librarymanagementsystem.fragment.ProfileFragment
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.core.content.ContextCompat

private const val HOME_ID = "HOME"
private const val TRANSACTION_ID = "TRANSACTION"
private const val STATISTIC_ID = "STATISTIC"
private const val PROFILE_ID = "PROFILE"

class ActivityLibrarian : AppCompatActivity() {

    private lateinit var pageNamePlaceholderTV: TextView

    private lateinit var homeBtn: Button
    private lateinit var transactionBtn: Button
    private lateinit var statisticBtn: Button
    private lateinit var profileBtn: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_librarian_base)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.activity_librarian_base)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val pageID = intent.getStringExtra("PAGE_ID") ?: HOME_ID
        Log.d("ActivityLibrarianBase", "pageID = $pageID")

        val currentUser = Firebase.auth.currentUser

        val userID = currentUser!!.uid


        pageNamePlaceholderTV = findViewById(R.id.pageNamePlaceholderTV2)
        homeBtn = findViewById(R.id.libHomeBtn)
        transactionBtn = findViewById(R.id.libTransBtn)
        statisticBtn = findViewById(R.id.libStatBtn)
        profileBtn = findViewById(R.id.libProfileBtn)

        loadActivity(pageID, userID)
        handleMenuButton(userID)
    }

    private fun loadActivity(pageID: String, userID: String) {
        if (pageID == PROFILE_ID) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, ProfileFragment())
                .addToBackStack(null)
                .commit()

            pageNamePlaceholderTV.text = "Profile"
            setMenuButtonColor(profileBtn, homeBtn, transactionBtn, statisticBtn)
        }

        else if (pageID == STATISTIC_ID) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, StatisticFragment())
                .addToBackStack(null)
                .commit()

            pageNamePlaceholderTV.text = "Statistic"
            setMenuButtonColor(statisticBtn, homeBtn, transactionBtn, profileBtn)
        }

        else if (pageID == TRANSACTION_ID) {
            val intent = Intent(this, LibrarianTransactionActivity::class.java)
            intent.putExtra("PAGE_ID", TRANSACTION_ID)
            startActivity(intent)
            finish()
            setMenuButtonColor(transactionBtn, homeBtn, statisticBtn, profileBtn)
        }

        else if (pageID == HOME_ID) {
            val intent = Intent(this, LibrarianHomeActivity::class.java)
            intent.putExtra("PAGE_ID", HOME_ID)
            startActivity(intent)
            finish()
            setMenuButtonColor(homeBtn, transactionBtn, statisticBtn, profileBtn)
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
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, StatisticFragment())
                .addToBackStack(null)
                .commit()

            pageNamePlaceholderTV.text = "Statistic"
        }

        profileBtn.setOnClickListener {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, ProfileFragment())
                .addToBackStack(null)
                .commit()

            pageNamePlaceholderTV.text = "Profile"
        }
    }

    private fun setMenuButtonColor(selectedBtn: Button, vararg deselectedBtns: Button) {
        // Set màu cho nút được chọn
        selectedBtn.apply {
            compoundDrawableTintList = ColorStateList.valueOf(ContextCompat.getColor(context, R.color.light_blue))
            setTextColor(ContextCompat.getColor(context, R.color.light_blue))
        }

        // Set màu cho tất cả nút không được chọn
        deselectedBtns.forEach { btn ->
            btn.apply {
                compoundDrawableTintList = ColorStateList.valueOf(ContextCompat.getColor(context, R.color.light_gray))
                setTextColor(ContextCompat.getColor(context, R.color.light_gray))
            }
        }
    }
}