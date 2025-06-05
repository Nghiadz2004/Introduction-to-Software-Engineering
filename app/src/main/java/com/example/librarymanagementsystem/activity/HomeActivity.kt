package com.example.librarymanagementsystem.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.librarymanagementsystem.R
import com.example.librarymanagementsystem.fragment.HomeFragment

class HomeActivity : AppCompatActivity() {

    private lateinit var myBookBtn: Button
    private lateinit var favoriteBtn: Button
    private lateinit var profileBtn: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_base_1)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Gắn HomeFragment lần đầu khi activity khởi động
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, HomeFragment())
            .commit()

        myBookBtn = findViewById(R.id.myBookBtn)
        favoriteBtn = findViewById(R.id.favoriteBtn)
        profileBtn = findViewById(R.id.profileBtn)

        myBookBtn.setOnClickListener {
            // Handle My Books button click
            val intent = Intent(this, ActivityBase2::class.java)
            intent.putExtra("PAGE_ID", "MYBOOK_ID")
            startActivity(intent)
            finish()
        }

        favoriteBtn.setOnClickListener {
            // Handle Favorites button click
            val intent = Intent(this, ActivityBase2::class.java)
            intent.putExtra("PAGE_ID", "MYFAVORITE_ID")
            startActivity(intent)
            finish()
        }

        profileBtn.setOnClickListener {
            // Handle Profile button click
            val intent = Intent(this, ActivityBase2::class.java)
            intent.putExtra("PAGE_ID", "PROFILE_ID")
            startActivity(intent)
            finish()
        }

    }
}