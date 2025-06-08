package com.example.librarymanagementsystem.activity

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.librarymanagementsystem.R
import com.example.librarymanagementsystem.fragment.MyFavoriteFragment
import com.example.librarymanagementsystem.fragment.ProfileFragment
import com.example.librarymanagementsystem.fragment.StatisticFragment
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import android.util.Log
import android.widget.Button
import android.widget.TextView

private const val HOME_ID = "HOME"
private const val MYBOOK_ID = "MYBOOK"
private const val MYFAVORITE_ID = "MYFAVORITE"
private const val PROFILE_ID = "PROFILE"

class ActivityBase2 : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var pageNamePlaceholderTV: TextView

    private lateinit var homeBtn: Button
    private lateinit var myBookBtn: Button
    private lateinit var favoriteBtn: Button
    private lateinit var profileBtn: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_base_2)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.activity_base_2)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val pageID = intent.getStringExtra("PAGE_ID") ?: HOME_ID
        Log.d("ActivityBase2", "pageID = $pageID")

        val userID = Firebase.auth.currentUser!!.uid

        pageNamePlaceholderTV = findViewById(R.id.pageNamePlaceholderTV)
        homeBtn = findViewById(R.id.homeBtn)
        myBookBtn = findViewById(R.id.myBookBtn)
        favoriteBtn = findViewById(R.id.favoriteBtn)
        profileBtn = findViewById(R.id.profileBtn)

//        if (pageID == "STATISTIC_ID") {
//            supportFragmentManager.beginTransaction()
//                .replace(R.id.fragment_container, StatisticFragment())
//                .commit()
//        }

        loadActivity(pageID!!, userID)
        handleMenuButton(userID)
    }

    private fun loadActivity(pageID: String, userID: String) {
        if (pageID == PROFILE_ID) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, ProfileFragment())
                .addToBackStack(null)
                .commit()

            pageNamePlaceholderTV.text = "Profile"
        }

//        else if (pageID == "MYBOOK_ID") {
//            supportFragmentManager.beginTransaction()
//                .replace(R.id.fragment_container, MyBookFragment())
//                .commit()
//        }

        else if (pageID == MYFAVORITE_ID) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, MyFavoriteFragment())
                .addToBackStack(null)
                .commit()
            pageNamePlaceholderTV.text = "My Favorite"
        }
    }

    private fun handleMenuButton(userID: String) {
        homeBtn.setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            intent.putExtra("PAGE_ID", HOME_ID)
            startActivity(intent)
            finish()
        }

//        myBookBtn.setOnClickListener {
//
//        }

        favoriteBtn.setOnClickListener {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, MyFavoriteFragment())
                .addToBackStack(null)
                .commit()

            pageNamePlaceholderTV.text = "My Favorite"
        }

        profileBtn.setOnClickListener {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, ProfileFragment())
                .addToBackStack(null)
                .commit()

            pageNamePlaceholderTV.text = "Profile"
        }
    }
}