//package com.example.librarymanagementsystem.activity
//
//import android.os.Bundle
//import androidx.activity.enableEdgeToEdge
//import androidx.appcompat.app.AppCompatActivity
//import androidx.core.view.ViewCompat
//import androidx.core.view.WindowInsetsCompat
//import com.example.librarymanagementsystem.R
//import com.example.librarymanagementsystem.fragment.MyBookFragment
//import com.example.librarymanagementsystem.fragment.MyFavoriteFragment
//import com.example.librarymanagementsystem.fragment.ProfileFragment
//import com.example.librarymanagementsystem.fragment.StatisticFragment
//
//class ActivityBase2 : AppCompatActivity() {
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        enableEdgeToEdge()
//        setContentView(R.layout.activity_base_2)
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.activity_base_2)) { v, insets ->
//            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
//            insets
//        }
//
//        val pageID = intent.getStringExtra("PAGE_ID")
//
//        if (pageID == "Profile_ID") {
//            val userID = "example_user_id" // TODO: get userID from database
//            val profileFragment = ProfileFragment.newInstance(userID)
//            supportFragmentManager.beginTransaction()
//                .replace(R.id.fragment_container, profileFragment)
//                .commit()
//        }
//        else if (pageID == "MYBOOK_ID") {
//            supportFragmentManager.beginTransaction()
//                .replace(R.id.fragment_container, MyBookFragment())
//                .commit()
//        }
//        else if (pageID == "MYFAVORITE_ID") {
//            supportFragmentManager.beginTransaction()
//                .replace(R.id.fragment_container, MyFavoriteFragment())
//                .commit()
//        }
//        else if (pageID == "STATISTIC_ID") {
//            supportFragmentManager.beginTransaction()
//                .replace(R.id.fragment_container, StatisticFragment())
//                .commit()
//        }
//    }
//}