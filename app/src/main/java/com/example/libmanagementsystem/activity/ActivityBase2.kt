package com.example.libmanagementsystem.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.libmanagementsystem.R
import com.example.libmanagementsystem.fragment.MyFavoriteFragment
import com.example.libmanagementsystem.fragment.ProfileFragment
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.content.res.ColorStateList
import androidx.core.content.ContextCompat

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
            setMenuButtonColor(favoriteBtn, profileBtn)

            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, MyFavoriteFragment())
                .addToBackStack(null)
                .commit()

            pageNamePlaceholderTV.text = "My Favorite"
        }

        else if (pageID == PROFILE_ID) {
            setMenuButtonColor(profileBtn, favoriteBtn)

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
            setMenuButtonColor(favoriteBtn, profileBtn)

            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, MyFavoriteFragment())
                .addToBackStack(null)
                .commit()

            pageNamePlaceholderTV.text = "My Favorite"
        }

        profileBtn.setOnClickListener {
            setMenuButtonColor(profileBtn, favoriteBtn)

            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, ProfileFragment())
                .addToBackStack(null)
                .commit()

            pageNamePlaceholderTV.text = "Profile"
        }
    }

    private fun setMenuButtonColor(selected_btn: Button, deselected_btn: Button) {
        Log.e("MENUBUTTON", selected_btn.toString())
        Log.e("MENUBUTTON", deselected_btn.toString())
        // Set icon color
        selected_btn.compoundDrawableTintList = ColorStateList.valueOf(ContextCompat.getColor(this, R.color.light_blue))
        deselected_btn.compoundDrawableTintList = ColorStateList.valueOf(ContextCompat.getColor(this, R.color.light_gray))

        // Set text color
        selected_btn.setTextColor(ContextCompat.getColor(this, R.color.light_blue))
        deselected_btn.setTextColor(ContextCompat.getColor(this, R.color.light_gray))
    }
}