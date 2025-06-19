package com.example.librarymanagementsystem.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.librarymanagementsystem.R
import com.example.librarymanagementsystem.fragment.MyFavoriteFragment
import com.example.librarymanagementsystem.fragment.ProfileFragment
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.content.res.ColorStateList
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.example.librarymanagementsystem.service.UIService
import kotlinx.coroutines.launch

private const val HOME_ID = "HOME"
private const val MYBOOK_ID = "MYBOOK"
private const val MYFAVORITE_ID = "MYFAVORITE"
private const val PROFILE_ID = "PROFILE"

class ActivityBase2 : AppCompatActivity() {
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

        loadActivity(pageID)
        handleMenuButton()
    }

    @SuppressLint("SetTextI18n")
    private fun loadActivity(pageID: String) {
        if (pageID == MYFAVORITE_ID) {
            lifecycleScope.launch {
                UIService.setButtonColor(
                    this@ActivityBase2,
                    favoriteBtn,
                    listOf(profileBtn)
                )
            }

            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, MyFavoriteFragment())
                .addToBackStack(null)
                .commit()

            pageNamePlaceholderTV.text = "My Favorite"
        }

        else if (pageID == PROFILE_ID) {
            lifecycleScope.launch {
                UIService.setButtonColor(
                    this@ActivityBase2,
                    profileBtn,
                    listOf(favoriteBtn)
                )
            }

            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, ProfileFragment())
                .addToBackStack(null)
                .commit()

            pageNamePlaceholderTV.text = "Profile"
        }
    }

    @SuppressLint("SetTextI18n")
    private fun handleMenuButton() {
        homeBtn.setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            intent.putExtra("PAGE_ID", HOME_ID)
            startActivity(intent)
            finish()
        }

        myBookBtn.setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            intent.putExtra("PAGE_ID", MYBOOK_ID)
            startActivity(intent)
            finish()
        }

        favoriteBtn.setOnClickListener {
            lifecycleScope.launch {
                UIService.setButtonColor(
                    this@ActivityBase2,
                    favoriteBtn,
                    listOf(profileBtn)
                )
            }

            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, MyFavoriteFragment())
                .addToBackStack(null)
                .commit()

            pageNamePlaceholderTV.text = "My Favorite"
        }

        profileBtn.setOnClickListener {
            lifecycleScope.launch {
                UIService.setButtonColor(
                    this@ActivityBase2,
                    profileBtn,
                    listOf(favoriteBtn)
                )
            }
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, ProfileFragment())
                .addToBackStack(null)
                .commit()

            pageNamePlaceholderTV.text = "Profile"
        }
    }
}